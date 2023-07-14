<template>
	<view class="content">
		<view>
			<u-modal :show="show" @confirm="show = false" :title="brand.brand" :content='brand.info'>
				<scroll-view class="notice-scroll" :scroll-y="true" :scroll-with-animation="true">
					<view class="model">
						<image :src="baseURL + brand.img"></image>
						<rich-text :nodes="brand.info" style="font-size: 26rpx;text-align: left;"></rich-text>
					</view>
				</scroll-view>
			</u-modal>
		</view>
		<view class="title">
			<span @click="toBrand()">< 返回</span>
			<span>{{brand.brand}}</span>
			<span @click="show = true">品牌介绍</span>
		</view>
		<view class="list" style="padding: 0;">
			<view class="card-list">
				<span>最新热评</span>
			</view>
		</view>
		
		<view class="swiper-frame" v-if="idx >= 0">
			<u-swiper class="swiper" :list="hotCarList" keyName="img" indicator indicatorMode="line" height="210"
				interval="5000" circular @change="changeSwiper"></u-swiper>
			<view class="swiper-text">
				<u--text :prefixIcon="hotCarList[idx].userImg" size="20" class="text" :lines="3" :text="':' + hotCarList[idx].evaluate"></u--text>
				<u-rate count="5" v-model="hotCarList[idx].score"></u-rate>
				<span class="price">日租:￥{{hotCarList[idx].price}}</span>
			</view>
		</view>
		<view v-else>
			<u-empty
					:text="evaluate"
			        icon="../../static/empty/no_evaluate.png"
			>
			</u-empty>
		</view>
		<view class="list">
			<view class="type-list">
				<span :class="key == type ? 'type' : ''" v-for="(item, key) in typeList"
					@click="switchTab(key)">{{item}}</span>
			</view>
			<view class="card-list" v-show="type == 0 || type == 1">
				<span>轿车</span>
				<view class="card-item" v-for="(item, key) in modelList" v-if="item.type == '轿车'" @click="toCar(item)">
					<image :src="baseURL + item.img"></image>
					<view class="bottom">
						<span class="name">{{item.model}}</span>
						<br>
						<p class="market-price">市售价：{{item.marketPrice}} </p>
						<view class="bottom-label">
							<view class="label vip">
								<image src="../../static/details/vip.png"></image>
								<span>会员免押</span>
							</view>
							<view class="label car-green"
								v-if="item.energy == '插混' || item.energy == '增程' || item.energy == '纯电' ">
								<image src="../../static/green_car.png"></image>
								<span>新能源</span>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="card-list" v-show="type == 0 || type == 2">
				<span>SUV</span>
				<view class="card-item" v-for="(item, key) in modelList" v-if="item.type == 'SUV'" @click="toCar(item)">
					<image :src="baseURL + item.img"></image>
					<view class="bottom">
						<span class="name">{{item.model}}</span>
						<br>
						<p class="market-price">市售价：{{item.marketPrice}} </p>
						<view class="bottom-label">
							<view class="label vip">
								<image src="../../static/details/vip.png"></image>
								<span>会员免押</span>
							</view>
							<view class="label car-green"
								v-if="item.energy == '插混' || item.energy == '增程' || item.energy == '纯电' ">
								<image src="../../static/green_car.png"></image>
								<span>新能源</span>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="card-list" v-show="type == 0 || type == 3">
				<span>MPV</span>
				<view class="card-item" v-for="(item, key) in modelList" v-if="item.type == 'MPV'" @click="toCar(item)">
					<image :src="baseURL + item.img"></image>
					<view class="bottom">
						<span class="name">{{item.model}}</span>
						<br>
						<p class="market-price">市售价：{{item.marketPrice}}</p>
						<view class="bottom-label">
							<view class="label vip">
								<image src="../../static/details/vip.png"></image>
								<span>会员免押</span>
							</view>
							<view class="label car-green"
								v-if="item.energy == '插混' || item.energy == '增程' || item.energy == '纯电' ">
								<image src="../../static/green_car.png"></image>
								<span>新能源</span>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="card-list" v-show="type == 0 || type == 4">
				<span>新能源</span>
				<view class="card-item" v-for="(item, key) in modelList"
					v-if="item.energy == '插混' || item.energy == '增程' || item.energy == '纯电' " @click="toCar(item)">
					<image :src="baseURL + item.img"></image>
					<view class="bottom">
						<span class="name">{{item.model}}</span>
						<br>
						<p class="market-price">市售价：{{item.marketPrice}} </p>
						<view class="bottom-label">
							<view class="label vip">
								<image src="../../static/details/vip.png"></image>
								<span>会员免押</span>
							</view>
							<view class="label car-green"
								v-if="item.energy == '插混' || item.energy == '增程' || item.energy == '纯电' ">
								<image src="../../static/green_car.png"></image>
								<span>新能源</span>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="card-list" v-show="type == 0 || type == 5">
				<span>其他</span>
				<view class="card-item" v-for="(item, key) in modelList" v-if="item.type == '其他'" @click="toCar(item)">
					<image :src="baseURL + item.img"></image>
					<view class="bottom">
						<span class="name">{{item.model}}</span>
						<br>
						<p class="market-price">市售价：{{item.marketPrice}}</p>
						<view class="bottom-label">
							<view class="label vip">
								<image src="../../static/details/vip.png"></image>
								<span>会员免押</span>
							</view>
							<view class="label car-green"
								v-if="item.energy == '插混' || item.energy == '增程' || item.energy == '纯电' ">
								<image src="../../static/green_car.png"></image>
								<span>新能源</span>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				idx: -1,
				duration: 5030,
				show: false,
				timer: null,
				typeList: ['全部', '轿车', 'SUV', 'MPV', '新能源', '其他'],
				type: 0,
				brand: {},
				modelList: [],
				hotCarList: [],
				evaluate:'暂无当前品牌最新评价'
			}
		},
		onLoad: function() {
			var that = this;
			that.brand = uni.getStorageSync("brand");
			this.getModelList();
			this.getHotCarList();
		},
		methods: {
			changeSwiper(e){
				//console.log(e)
				this.idx = e.current;
			},
			getModelList() {
				let that = this;
				uni.request({
					url: this.baseURL + '/vehicle/findModelList?brandId=' + this.brand.id,
					method: 'GET', //请求方式，必须为大写
					success: (res) => {
						console.log('/vehiche/findModelList', res);
						if (res.data.code == 1) {
							console.log(res.data.data)
							that.modelList = res.data.data;
						} else {
							uni.showToast({
								title: res.data.msg,
								icon: 'none',
								duration: 2000
							});
						}
					}
				})
			},
			getHotCarList() {
				let that = this;
				uni.request({
					url: this.baseURL + '/vehicle/findHotCarList?brand=' + this.brand.brand,
					method: 'GET', //请求方式，必须为大写
					success: (res) => {
						console.log('/vehiche/findHotCarList', res);
						if (res.data.code == 1) {
							console.log(res.data.data)
							that.hotCarList = res.data.data;
							let len = that.hotCarList.length;
							if (len == 0) 
								that.idx = -1;
							else 
								that.idx = 0;
							for (let i = 0; i < len; i++) {
								that.hotCarList[i].img = that.baseURL + that.hotCarList[i].img
								//that.hotCarList[i].userImg = that.baseURL + that.hotCarList[i].userImg
							}
						} else {
							// uni.showToast({
							// 	title: res.data.msg,
							// 	icon: 'none',
							// 	duration: 2000
							// });
						}
					}
				})
			},
			toBrand() {
				uni.switchTab({
					url: '../brand/brand'
				})
			},
			toCar(item) {
				uni.switchTab({
					url: '../car/car'
				})
			},
			switchTab(i) {
				this.type = i;
			}
		}
	}
</script>

<style lang="scss">
	.content {
		width: 750rpx;
		min-height: 100vh;
		background-color: #f8f7fe
	}

	.title {
		width: 750rpx;
		height: 100rpx;
		line-height: 100rpx;
		padding-top: 60rpx;
		color: #464646;
		background-color: #fff;
	}

	.title view {
		display: inline-block;
		width: 220rpx;
		height: 120rpx;
	}

	.title view image {
		display: inline-block;
		width: 20rpx;
		height: 20rpx;
		vertical-align: middle;
	} 

	.title span {
		font-size: 36rpx;
		font-weight: 650;
		width: 220rpx;
		display: inline-block;
		text-align: center;
		padding: 0 15rpx;
		vertical-align: middle;
	}

	.title span:first-child {
		text-align: left;
		font-weight: 450;
		font-size: 36rpx;
	}

	.title span:last-child {
		text-align: right;
		font-weight: 450;
		font-size: 32rpx;
	}

	.swiper-frame {
		padding: 20rpx 36rpx;
		width: 678rpx;
		background-color: #f5f5f6;
		position: relative;
	}

	.swiper-frame .swiper {
		width: 280rpx;
	}

	.swiper-frame .swiper-text {
		position: absolute;
		width: 400rpx;
		top: 18rpx;
		right: 0rpx;
		padding: 10rpx;
	}

	.swiper-frame .swiper-text .text {
		height: 160rpx;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.swiper-frame .swiper-text span {

		font-size: 30rpx;
		margin-top: 4rpx;
		padding: 2rpx 4rpx;
		border-radius: 2rpx;
	}

	.swiper-frame .swiper-text .count {
		float: left;
		background-color: #55aaff;
		color: #f9f8e2;
		font-size: 24rpx;
	}

	.swiper-frame .swiper-text .price {
		float: right;
		margin-top: -26rpx;
		background-color: #fff6dd;
		color: #ff6769;
		font-weight: 550;
		font-size: 22rpx;
	}

	.list {
		padding: 20rpx;

	}

	.type-list {
		text-align: center;
		margin: 12rpx 2rpx;
	}

	.type-list>span {
		font-size: 30rpx;
		padding: 6rpx 16rpx;
		margin: 0 12rpx;
		border-radius: 4rpx;
		border: #f0f0f3 1rpx solid;
		background-color: #f0f0f3;
		color: #565656;
		font-size: 24rpx;
	}

	.type-list .type {
		border: #a1a1a1 1rpx solid;
		background-color: #fff;
		color: #000;
	}

	.list .card-list {
		text-align: center;
	}

	.list .card-list>span {
		font-size: 23rpx;
		color: #757575;
	}

	.list .card-list .card-item {
		text-align: left;
		width: 650rpx;

		padding: 20rpx;
		margin: 16rpx 10rpx;
		background-color: #ffffff;
		border-radius: 6rpx;
		box-shadow: 1rpx 1rpx 10rpx #f6f6f6;
	}

	.list .card-list .card-item image {
		display: inline-block;
		width: 240rpx;
		height: 135rpx;
		vertical-align: middle;
	}

	.list .card-list .card-item .bottom {
		display: inline-block;
		width: 340rpx;
		margin-left: 50rpx;
		vertical-align: middle;
	}

	.list .card-list .card-item .bottom .name {
		font-size: 28rpx;
		font-weight: 550;
		margin-bottom: 20rpx;
	}

	.list .card-list .card-item .bottom .market-price {
		font-size: 20rpx;
		overflow: hidden;
	}

	.list .card-list .card-item .bottom .bottom-label {
		display: inline-block;
		width: 340rpx;
		font-size: 16rpx;
	}

	.list .card-list .card-item .bottom .bottom-label .label {
		border-radius: 10rpx;
		display: inline-block;

		margin: 16rpx 6rpx;
		padding: 3rpx;
	}

	.list .card-list .card-item .bottom .bottom-label .label image {
		width: 34rpx;
		height: 34rpx;
		display: inline-block;
		margin-right: 6rpx;
		vertical-align: bottom;
	}

	.list .card-list .card-item .bottom .bottom-label .label span {
		vertical-align: bottom;
		font-size: 22rpx
	}

	.model {
		text-align: center;
		height: 500rpx;
	}

	.model>image {
		width: 250rpx;
		height: 250rpx;
	}
</style>