<template>
	<view>
		<u-popup :show="noPay" mode="bottom" @close="close" @open="open" closeable round="20">
			<view class="pay">
				<view class="price">
					￥<span>{{order.paymentAmount}}</span>
				</view>
				<view class="username">
					<span class="fl">账号</span>
					<span class="fr">{{user.username}}</span>
				</view>
				<view class="payment">
					<span class="fl">支付方式</span>
					<span class="fr">{{payment.name}}</span>
					<image class="fr" :src="payment.img"></image>
				</view> 
				<view class="payment-list">
					<span class="title">选择支付方式</span>
					<view class="payment-item" v-for="(item, idx) in paymentList" @click="changePayment(item)">
						<image class="fl" :src="item.img"></image>
						<span class="fl">{{item.name}}</span>
						<span class="fl" v-if="item.type == 1">({{user.salary}}元)</span>
						<image class="fr" src="../static/common/dui_3.png" v-show="item.id == payment.id"></image>
					</view>
				</view>
				<view class="button" @click="confirm">
					立即支付
				</view>
			</view>
		</u-popup>
		<u-modal showCancelButton :show="payConfirm" @confirm="payTrue" @cancel="payFalse" 
				confirmText="确认支付" cancelText="我再想想"
				ref="uModal" :title="title" :content='content'></u-modal>
	</view>
</template>

<script>
	export default {
		name: "payPopUp",
		props: ['noPay', 'user', 'coupon', 'order'],
		data() {
			return {
				radio: true,
				payment:{
					id: 1,
					name:'余额',
					type:1,
					img:'../../static/common/yue.png'
				},
				paymentList:[
					{
						id: 1,
						name:'余额',
						type:1,
						img:'../../static/common/yue.png'
					},
					{
						id: 2,
						name:'微信支付',
						type:2,
						img:'../../static/common/weixin_pay.png'
					},
					{
						id: 3,
						name:'支付宝',
						type:3,
						img:'../../static/common/zhifubao.png'
					},
					{
						id: 4,
						name:'银行卡',
						type:4,
						img:'../../static/common/bank_card.png'
					},
				],
				payConfirm: false,
				title:"支付确认",
				content:''
			}
		},
		methods: {
			confirm(){
				if(this.payment.id == 1 && this.user.salary < this.order.paymentAmount){
					uni.showToast({
						icon:'none',
						title:'余额不足',
					}, 1500);
					return;
				}
				this.content = "确认支付金额：" + this.order.paymentAmount + " 元吗？";
				this.payConfirm = true;
			},
			payTrue(){
				let that = this;
				this.order.payChannel = this.payment.id;
				this.order.couponUserId = this.coupon.id;
				uni.request({
					url: that.baseURL + '/order/createOrder',
					data: this.order,
					withCredentials:true,
					xhrFields: {
						withCredentials: true
					},
					method: 'POST',
					success(res) {
						console.log(res);
						that.order = res.data.data
						if(that.order.status == 0){
							uni.$emit('orderObligation', that.order);
						}else if(that.order.status == 1){
							uni.$emit('orderInProgress', that.order);
						}
						that.payConfirm = false;
					}
				})
			},
			payFalse(){
				let that = this;
				
			},
			changePayment(item){
				this.payment = item; 
			},
			open() {

			}, 
			close() {
				this.$emit('getNoPay', false);
			},
			
		}
	}
</script>

<style>
	.pay{
		text-align: center;
		margin: 20rpx 0 80rpx;
	}
	.pay image{
		width: 46rpx;
		height: 46rpx;
		line-height: 46rpx;
		margin-right: 2rpx;
	}
	.pay .price{
		margin-top: 60rpx;
		margin-bottom: 120rpx;
		font-size: 52rpx;
	}
	.pay .price>span{
		font-size: 76rpx;
		font-weight: 650;
	}
	.pay .username,
	.pay .payment{
		padding: 20rpx 0;
		margin: 0 36rpx;
		overflow: hidden;
		color: #5e5e5e;
		height: 46rpx;
		line-height: 46rpx;
	}
	.pay .username{
		border-bottom: #e2e2e2 1rpx solid;
	}
	.pay .payment-list{
		margin: 10rpx 36rpx 50rpx;
		padding: 20rpx;
		background-color: #f9f9f9;
		border-radius: 20rpx;
		text-align: left;
		overflow: hidden;
		color: #5e5e5e;
	}

	.pay .payment-list .title{
		font-size: 26rpx;
		font-weight: 650;
		margin: 0 6rpx;
	}
	.pay .payment-list .payment-item{
		height: 46rpx;
		line-height: 46rpx;
		font-size: 30rpx;
		padding: 26rpx 10rpx;
		border-bottom: #e2e2e2 1rpx solid;
	}
	.pay .payment-list .payment-item span{
		padding: 0 3rpx;
	}
	.pay .button{
		display: inline-block;
		font-size: 36rpx;
		color: aliceblue;
		width: 540rpx;
		height: 80rpx;
		line-height: 80rpx;
		background-color: #0055ff;
		border-radius: 20rpx;
		margin-bottom: 30rpx;
		padding: 4rpx 0;
		box-shadow: 0 20rpx 40rpx #dce7ff;
	}
	.pay .others{
		display: flex;
		margin: 15rpx 235rpx;
		width: 280rpx;
		margin-bottom: 60rpx;
		justify-content: space-around;
	}
	.pay .others>image{
		display: inline-block;
		width: 66rpx;
		height: 66rpx;
	}
	.pay .footer{
		display: inline-block;
		width: 580rpx;
	}
	.pay .footer .radio{
		display: inline-block;
		width: 26rpx;
		height: 26rpx;
		border-radius: 26rpx;
		border: 2rpx solid #020202;
		margin-right: 10rpx;
	}
	.pay .footer>span{
		display: inline-block;
		font-size: 22rpx;
	}
	.pay .footer>span>span{
		color: #00aaff;
	}
</style>