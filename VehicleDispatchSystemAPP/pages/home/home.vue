<template>
	<view class="content">
		<view class="head">
			<image class="head-img" :src="user.img" v-if="text == '退出登录'"></image>
			<image class="head-img" src="../../static/login/login_default.png" @click="login = true" style="background-color: #fff;;" v-else></image>
			<image class="driver-card" src="../../static/home/card_hidden.png" v-if="!isValue(user.driverType)"></image>
			<image class="driver-card" src="../../static/home/card_display.png" v-else></image>
			<image class="driver-car" src="../../static/home/car_hidden.png"></image>
			<br>
			<view>
				<span class="vip" v-if="user.vipLevel > 0">vip{{user.vipLevel}}</span>
				<span class="nickname">{{user.nickname}}</span>
			</view>
			
		</view>
		<view class="card">
			<view class="title">
				<span class="fl">我的订单 | </span>
				<span class="fl" style="padding: 0 10rpx;margin-left: 10rpx;background-color: #f5f5f5;border-radius: 6rpx;" v-if="redDot == true" @click="redDot = false"> 弱化消息通知</span>
				<span class="fl" style="padding: 0 10rpx;margin-left: 10rpx;background-color: #f5f5f5;border-radius: 6rpx;" v-else @click="redDot = true"> 强化消息通知</span>
				<span class="fr">全部 > </span>
			</view>
			<view class="body">
				<view class="body-item" v-for="(item, idx) in orderTypes">
					<image :src="item.img"></image>
					<view :class="{'red-dot' : redDot, 'grey-dot' : !redDot}" v-show="idx == 0 && orderObligation.num > 0">{{orderObligation.num}}</view>
					<view :class="{'red-dot' : redDot, 'grey-dot' : !redDot}" v-show="idx == 1 && orderInProgress.num > 0">{{orderInProgress.num}}</view>
					<!-- <view :class="{'red-dot' : redDot, 'grey-dot' : !redDot}" v-show="idx == 2 && orderComplete.num > 0">{{orderComplete.num}}</view>
					<view :class="{'red-dot' : redDot, 'grey-dot' : !redDot}" v-show="idx == 3 && orderCancel.num > 0">{{orderCancel.num}}</view> -->
					<br>
					<span>{{item.text}}</span>
				</view>
			</view>
			<view class="real-name">
				<image src="../../static/home/card_hidden.png"></image>
				<view class="body-item">
					<span>请完善实名信息</span>
					<br>
					<span>认证身份信息后可提车</span>
				</view>
				<span class="button">去完善</span>
			</view>
		</view>
		<view class="card">
			<view class="title">
				<span class="fl">更多</span>
			</view>
			<view class="body">
				<view class="body-item" v-for="(item, idx) in otherTypes">
					<image :src="item.img"></image>
					<br>
					<span>{{item.text}}</span>
				</view>
			</view>
			
		</view>
		<view class="click" @click="backLogin = true" v-if="text == '退出登录'">
			<span>{{text}}</span>
		</view>
		<view class="click" @click="login = true" v-else>
			<span>{{text}}</span>
		</view>
		<u-modal showCancelButton :show="backLogin" title="退出登录" :content='content' @cancel="backLogin = false" @confirm="goBack()"></u-modal>	
		<loginPopUp :login="login" @getLogin="getLogin" @getUser="getUser"></loginPopUp>
	</view>
    
</template>

<script>
	import loginPopUp from '../../components/loginPopUp.vue'
    export default {
        data() {
            return {
                user:{},
				redDot: false,
				orderTypes:[
					{img:'../../static/home/pay.png', text:'待付款'},
					{img:'../../static/home/ing.png', text:'进行中'},
					{img:'../../static/home/finish.png', text:'已完成'},
					{img:'../../static/home/cancel.png', text:'已取消'},
				],
				otherTypes:[
					{img:'../../static/home/collect.png', text:'收藏车辆'},
					{img:'../../static/home/coupon.png', text:'优惠券'},
					{img:'../../static/home/manage.png', text:'联系管理'},
					{img:'../../static/home/feedback.png', text:'问题反馈'},
					{img:'../../static/home/about.png', text:'开发人员'},
				],
				text:'',
				content:'是否退出登录？',
				backLogin: false,
				login:false,
				orderObligation:{
					num: 0,
					list: [],
				}, 
				orderInProgress:{
					num: 0,
					list: [],
				},
				orderComplete:{
					num: 0,
					list: [],
				},
				orderCancel:{
					num: 0,
					list: [],
				},
			};
        },
		components:{
			"loginPopUp":loginPopUp
		},
        onShow() {
			this.isLogin()
			uni.$on('orderObligation', (e)=>{
				console.log(e)
				this.orderObligation.list.push(e);
				this.orderObligation.num ++;
			});
		},
        onLoad() {
			this.isLogin()
		},
        methods: {
			isLogin(){
				this.user = uni.getStorageSync("user");
				console.log('打印相关信息', this.user);
				
				if(JSON.stringify(this.user)=="{}" || this.user.username == ''|| this.user.username == undefined){
					this.text = '登录'
				}else{
					this.text = '退出登录'
				}
			},
			gePreValue(e){
				this.getLogin(e.login);
				this.getUser(e.user);
			},
			getLogin(login){
				this.login = login;
			},
			getUser(user){
				this.user = user;
				if(JSON.stringify(this.user)=="{}" || this.user.username == ''|| this.user.username == undefined){
					this.text = '登录'
				}else{
					this.text = '退出登录'
				}
			},
			goBack(){
				var that = this;
				uni.request({
					url:this.baseURL + '/login/userLogout?id=' + this.user.id,
					withCredentials:true,
					xhrFields: {
						withCredentials: true
					},
					method:'GET',//请求方式，必须为大写
					success: (res) => {
						console.log('接口返回------',res);
						uni.showToast({
							title: res.data.msg,
							icon:'none',
							duration: 2000
						});
						if(res.data.code == 1){
							uni.removeStorageSync("user");
							that.user = uni.getStorageSync("user");
							that.text = '登录'
							that.backLogin = false
						}else{
							
						}
					}
				})
			},
			toLogin(){
				uni.showToast({
					title: '即将跳转登录页面',
					icon:'none',
					duration: 1000
				})
				setTimeout(() => {
					uni.navigateTo({
					     url: '../login/login'
					});
				}, 1000)
				
			},
			
		},

    };
</script>

<style scoped="scoped" lang="scss">
	.content {
		background-color: #ebeef6;
		padding-bottom: 60rpx;
		text-align: center;
		min-height: 100vh;
	}
    .head{
		width: 750rpx;
		text-align: center;
		padding: 200rpx 0;
		position: relative;
	}
	.head .head-img{
		width: 260rpx;
		height: 260rpx;
		border-radius: 130rpx;
		margin: 20rpx 0;
	}
	.head .driver-card{
		position: absolute;
		width: 48rpx;
		height: 48rpx;
		top: 434rpx;
		left: 448rpx;
	}
	.head .driver-car{
		position: absolute;
		width: 48rpx;
		height: 48rpx;
		top: 434rpx;
		left: 504rpx;
	}
	.nickname{
		text-align: center;
		color: #666;
	}
	.vip{
		font-size: 28rpx;
		background-image: linear-gradient(to right, #262639 , #565658);
		color: #ffdf7d;
		border-radius: 14rpx;
		padding: 0 14rpx 0 8rpx;
		margin-right: 6rpx;
		font-weight: bold;
		font-style: italic;
	}
	.card{
		display: inline-block;
		width: 660rpx;
		background-color: #fff;
		border-radius: 40rpx;
		padding: 20rpx;
		box-shadow: 0 80rpx 60rpx #e8ebf6;
		margin-bottom: 40rpx;
	}
	.card .title{
		font-size: 26rpx;
		color: #666;
		overflow: hidden;
	}
	.card .body{
		width: 100%;
		display: flex;
		flex: 1;
	}
	.card .body .body-item{
		margin: 50rpx 0 0;
		width: 160rpx;
		height: 120rpx;
		font-size: 24rpx;
		position: relative;
	}
	.card .body .body-item image{
		width: 60rpx;
		height: 60rpx;
	}
	.card .body .body-item view{
		position: absolute;
		display: inline-block;
		min-width: 36rpx;
		border-radius: 30rpx;
		margin-left: -20rpx;
		top: -10rpx;
	}
	.red-dot{
		color: #fffff9;
		background-color: #fc0922;
		transition-duration: 1s;
	}
	.grey-dot{
		color: #fffff9;
		background-color: #cccccc;
		transition-duration: 1s;
	}
	.card .real-name{
		display: inline-block;
		width: 640rpx;
		background-color: #f5f5f6;
		border-radius: 20rpx;

		height: 100rpx;
	}
	.card .real-name image{
		width: 60rpx;
		height: 60rpx;
		margin: 20rpx 0 20rpx 0;
		vertical-align: middle;
	}
	.card .real-name view{
		display: inline-block;
		width: 300rpx;
		height: 72rpx;
		font-size: 26rpx;
		text-align: left;
		margin: 0 40rpx;
		vertical-align: middle;
	}
	.card .real-name view>span:first-child{
		font-weight: 700;
	}
	.card .button{
		background-color: #5555ff;
		padding: 8rpx 16rpx;
		border-radius: 30rpx;
		color: #fff;
		font-size: 28rpx;
		vertical-align: middle;
	}
	.click{
		margin-top: 100rpx;
		height: 80rpx;
		line-height: 80rpx;
		color: #ef4236;
		background-color: #fff
	}
</style>