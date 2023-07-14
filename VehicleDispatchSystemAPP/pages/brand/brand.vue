<template>
	<view>
		<view class="title">
			<span>车辆品牌</span>
		</view>
		<u-search :showAction="false" placeholder="请输入搜索内容" v-model="select" bgColor="#fff" borderColor="#eeeeee"></u-search>
		<view v-for="(item, idx) in brandSum" :key="idx">
			<view class="initial">
				<span>{{item.initial}}</span>
			</view>
			<u-cell-group>
				<view v-for="(brand, i) in item.brandList" :key="brand.id" @click="toModel(brand)" v-show="select == '' || brand.brand.indexOf(select) >= 0 || select.toUpperCase() == item.initial">
					<u-cell arrow-direction="down" :title="brand.brand">
						<u-icon slot="icon" size="80" :name="baseURL + brand.img"></u-icon>
						<!-- <u-badge count="99" :absolute="false" slot="right-icon"></u-badge> -->
					</u-cell>
				</view>
			</u-cell-group>
		</view>
			
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				select:'',
				brandSum:[],
				
			}
		},
		onLoad(){
			this.listBrand()
		},
		onShow(){
			this.listBrand()
		},
		methods: {
			listBrand(name = ''){
				let that = this;
				uni.request({
						url: this.baseURL + "/vehicle/listBrand?name=" + name,
						header: {
							// 'Content-Type': 'application/x-www-form-urlencoded'
							 'Content-Type': 'application/json' //自定义请求头信息
						},
						method:'GET',//请求方式，必须为大写
						success: (res) => {
							console.log('/vehiche/listBrand',res);
							if(res.data.code == 1){
								console.log(res.data.data)
								that.brandSum = res.data.data;
							}
							else{
								uni.showToast({
									title: res.data.msg,
									icon:'none',
									duration: 2000
								});
							}
						}
					})
				
			},
			toModel(item){
				uni.setStorageSync("brand", item);
				uni.navigateTo({
				     url: '../model/model'
				});
			}
		}
	}
</script>

<style>
	.title{
		width: 750rpx;
		height: 100rpx;
		text-align: center;
		line-height: 100rpx;
		margin-top: 60rpx;
		color: #464646;
	}
	.title span{
		font-size: 36rpx;
		font-weight: 650;
	}
	.initial{
		width: 730rpx;
		padding: 0 10rpx;
		background-color: #ededed;
		color: #6d6d6d;
	}
</style>
