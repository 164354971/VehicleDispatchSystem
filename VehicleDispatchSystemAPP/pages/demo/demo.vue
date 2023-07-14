<template>
	<view class="page">
		<view class="login">
			<!-- <view class="header"><image src="/static/img/weixin.png"></image></view> -->
			<view class="content">
				<view class="title">legao 申请使用</view>
				<view class="info">你的微信头像、昵称、地区和性别信息</view>
			</view>
			<button class="bottom" type="primary" open-type="getUserInfo" withCredentials="true" lang="zh_CN" @click="handleThirdLoginApp">授权登录</button>
			<button type="default" class="refuse" @click="goback">拒绝</button>
		</view>
		<u-toast ref="uToast" />
	</view>
</template>
<script>
export default {
	data() {
		return {};
	},
	methods: {
		goback() {
			uni.navigateTo({
				url: '/pages/login/login'
			});
		},
		//app第三方登录

		handleThirdLoginApp() {
			var that = this;
			uni.getProvider({
				service: 'oauth',
				success: function(res) {
					if (~res.provider.indexOf('weixin')) {
						uni.login({
							provider: 'weixin',
							success: function(loginRes) {
								// loginRes 包含access_token，expires_in，openid，unionid等信息
								//这里只需要把这些数据传递给后台，让后台去请求微信的接口拿到用户信息就可以了，
								//然后返回登录状态
								that.getApploginData(loginRes); //请求登录接口方法
							},

							fail: function(res) {
								that.$refs.uToast.show({
									title: '微信登录失败',
									type: 'warning'
								});
							}
						});
					}
				}
			});
		},

		//请求登录接口方法

		getApploginData(data) {
			var that = this;
			//这边是前端自己去调微信用户信息的接口，根据接口需要请求，如果不需要前端去获取的话就交给后端，可省去次操作
			uni.request({
				url: 'https://api.weixin.qq.com/sns/userinfo?access_token=' + data.authResult.access_token + '&openid=' + data.authResult.openid,
				method: 'GET',
				dataType: 'json',
				header: {
					'content-type': 'application/x-www-form-urlencoded' // 默认值
				},

				success(res) {
					console.log('【登录回调啾啾啾】', res);
					//前端调用微信接口，获取到微信用户的基本信息，传递给后台
					that.$api.wxloginThred({ unionid:data.authResult.unionid,image:res.data.headimgurl, nickname:res.data.nickname,}).then(res=>{
						console.log(res)
						if (res.statusCode == 200) {
							uni.setStorageSync('userInfo', JSON.stringify(res.data));
							uni.setStorageSync('logined', 1);
							that.$store.commit('SET_STATE', ['logined', true]);
							that.$store.commit('SET_STATE', ['userInfo', res.data]);
							uni.showToast({
								title: '登陆成功',
								duration: 2000
							});
							//登录成功 跳转到首页
							that.checkFirst()
						}
					});
				},
				fail() {
					that.$refs.uToast.show({
						title: '微信登录失败',
						type: 'warning'
					});
				}
			});
		},
		checkFirst(){
			this.$api.checkFirst({}).then(res=>{
				if(res.data == '1'){
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/login/begin'
						});
					}, 2000);
				}else{
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/index/index'
						});
					}, 2000);
				}
			})
		},
	}
};
</script>
