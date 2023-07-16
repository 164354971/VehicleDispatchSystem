import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
// main.js，注意要在use方法之后执行
import uView from 'uview-ui'

Vue.use(uView)

// 如此配置即可
uni.$u.config.unit = 'rpx'
Vue.config.productionTip = false
//Vue.prototype.baseURL = 'http://192.168.5.78:8888'
//Vue.prototype.baseURL = 'http://192.168.2.127:8888'
Vue.prototype.baseURL = 'http://192.168.10.71:8888' 
//Vue.prototype.baseURL = 'http://192.168.101.200:8888' 
Vue.prototype.getToken = function (){
  //
}
Vue.prototype.isValue = function (obj){
	if(obj == undefined || obj == null || obj == '' || obj == JSON.stringify(obj))
		return false;
	return true;
}

Vue.prototype.isUserLogin = async function (){
	let that = this;
	let user = uni.getStorageSync("user");
	console.log(user);
	if(!this.isValue(user)){
		user = {};
		user.id = 0;
	}
	await uni.request({
		url: this.baseURL + "/login/isUserLogin?id=" + user.id,
		withCredentials: true,
		xhrFields: {
			withCredentials: true
		},
		header: {
			// 'Content-Type': 'application/x-www-form-urlencoded'
			 'Content-Type': 'application/json' ,//自定义请求头信息
			 //Authorization: uni.getStorageSync('token')
		},
		method: 'GET', //请求方式，必须为大写
		success: (res) => {
			console.log('/login/isUserLogin', res.data);
			
			if (res.data.code == 1) {
				user = res.data.data;
				uni.setStorageSync("user", user)
				uni.setStorageSync("activeRadio", "用户");
			} else {
				uni.showToast({
					title: '用户未登录，即将跳转登录页',
					icon: 'none',
					duration: 2000
				});
				uni.removeStorageSync("user")
			}
		},
		complete() {
		}
	})

	
	
}
App.mpType = 'app'
const app = new Vue({
  ...App
})
app.$mount()
// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
export function createApp() {
  const app = createSSRApp(App)
  return {
    app
  }
}
// #endif