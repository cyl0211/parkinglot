<template>
<div class="search-card">
	<el-input v-model.trim="searchNumber" placeholder="请输入手机号查询停车卡"></el-input>
	<el-button type="primary" @click="searchCard()">查找</el-button>
	<el-table ref="singleTable"  
	highlight-current-row 
	:data="UserCardTable" 
	tooltip-effect="dark" 
	style="width: 100%" 
	@current-change="handleCurrentChange">
	    <el-table-column label="" width="100">
	    	<template slot-scope="scope"><img v-if="currentCardNum==scope.row.cardNum" src='../../../static/tick.jpg'></template>
	    </el-table-column>
	    <el-table-column label="卡号" width="220">
	    	<template slot-scope="scope">{{ scope.row.cardNum }}</template>
	    </el-table-column>
	    <el-table-column label="办理时间" width="220">
	    	<template slot-scope="scope">{{ secondToDate(scope.row.createdTime) }}</template>
	    </el-table-column>
	    <el-table-column label="停车场" width="220">
	    	<template slot-scope="scope">{{ scope.row.parkingName }}</template>
	    </el-table-column>
	    <el-table-column label="状态" show-overflow-tooltip>
			<template slot-scope="scope">{{ cardState[scope.row.state] }}</template>
	    </el-table-column>
	</el-table>
	<!-- 补办弹窗 -->
	<el-dialog title="补办" :visible.sync="cardFormVisible" :lock-scroll="false" >
      	<el-form :disabled="isDisabled">
      		<!-- <el-form-item label="月份" label-width="150">
      			<el-date-picker
      			v-model="priceform.month"
      			type="month"
      			placeholder="选择月份"
      			@change="output()">
      		    </el-date-picker>
      		</el-form-item> -->
      		<el-form-item label="停车场：" label-width="200" >
      			<span>{{currentParkingName}}</span>
      		</el-form-item>
      		<el-form-item label="当前卡号：" label-width="200" >
      			<span>{{currentCardNum}}</span>
      		</el-form-item>
      		<el-form-item label="补办卡号" label-width="100" >
      			<!-- <div class="tip" v-show="isDisabled">今日为出账日，禁止修改价格</div> -->
      			<el-input v-model="modifyCardNum" placeholder="请输入补办的卡号"></el-input>
      		</el-form-item>
      	</el-form>
      	<div slot="footer" class="dialog-footer">
      		<el-button @click="cardFormVisible = false">取 消</el-button>
      		<el-button type="primary" @click="modifyCard()" :disabled="isDisabled">提交修改</el-button>
      	</div>
      </el-dialog>
	<!-- 操作员操作停车卡按钮 -->
	<div class="operator-card">
		<el-button type="primary" @click="judgeChosenCard()">补办</el-button>
		<el-button type="primary" @click="deleteCard = true">注销</el-button>
	</div>
</div>
</template>

<script>
	export default{
		name: `SearchCard`,
		data(){
			return{
				imgUrl: `../../../static/tick.jpg`,
				searchNumber: ``,
				state:0,
				upadteCard:false,
				deleteCard:false,
				//卡状态
				cardState:[`正常`,`禁用`,`已注销`],
				UserCardTable:[],
				// 补办表单
				cardFormVisible: false,
				//补办显示
				isDisabled: false,
				//当前卡ID
				currentCardId: ``,
				//当前卡号
				currentCardNum: ``,
				//当前卡号对应的停车场
				currentParkingName: ``,
				//补办卡号
				modifyCardNum: ``,
			}
		},
		methods:{
			// 通过手机查询停车卡
			searchCard(){
				if(this.InputLimit(this.searchNumber))
				this.axios.post(this.baseURI + '/parkingCard/selectUserCardsForList', { "phone": this.searchNumber})
				.then( res => {
                    if(!res.data.data){
                    this.$notify({
                      title: '提示信息',
                      message: res.data.message,
                      type: 'error'
                    });
                  }
                  console.log(res.data);
                  // if(res.data.state==0){
                  		this.UserCardTable = res.data.data;
                  	// }
				})
				.catch( err => {
					console.log(err);
				})
			},
			//单选事件
			handleCurrentChange(val) {
				this.currentCardNum = val.cardNum;
				this.currentCardId = val.id;
				this.state=val.state;
				this.currentParkingName = val.parkingName
				console.log(this.currentCardNum + ` ` + this.currentCardId+` `+this.state);
			},
            //时间转换函数
            secondToDate(date){
            	let res = new Date(date).toLocaleString();
            	return res.slice(0,res.indexOf(' '));
            },
            //输入限制
            InputLimit(cardNum){
                //进入输入判断
                	if(!cardNum){
                		this.$notify({
                			title: '提示信息',
                			message: '请填写完整',
                			type: 'error'
                		});
                	}
                	else if(!(/^[0-9]*$/.test(cardNum))){
                		this.$notify({
                			title: '提示信息',
                			message: '请输入数字',
                			type: 'error'
                		});
                		console.log(123);
                	}else{ 
                		return true;
                	}
                    return false;
 			},
 			//补办
 			modifyCard(){
                 if(this.InputLimit(this.modifyCardNum))
                 	this.axios.post(this.baseURI + `/parkingCard/createNewCardReplaceOldOne`,{
                 		id: this.currentCardId,
                 		cardNum: this.modifyCardNum
                 	})
                 .then( res =>{
             		if(res.data.message == "OK"){
                        this.$notify({
                			title: '提示信息',
                			message: '补办成功！',
                			type: 'success'
                		});
                		this.searchCard();
                		this.cardFormVisible = false;
                 	}
                 	else{
   						this.$notify({
                			title: '提示信息',
                			message: res.data.message,
                			type: 'error'
                		})
                		console.log(res.data.message);
                 	}
                    
                 })
                 .catch( err =>{

                 })
 			},
 			//弹窗限制
 			judgeChosenCard(){
 				if(this.currentCardNum){
 					if(this.state==2){
 						this.$notify({
                			title: '提示信息',
                			message: '该卡已注销,无法补办!',
                			type: 'error'
                		});
 					}else{
 						this.cardFormVisible = true;
 					}
 					
 				}
 				else{
 					this.$notify({
            			title: '提示信息',
            			message: '请选择一个卡！',
            			type: 'error'
            		});
 				}
 			}
		}
	}
</script>

<style scoped>
	.search-card{
		width: 980px;
	}
	.el-input {
		display: inline-block;
		width: 230px;
		margin-bottom: 30px;
	}
	.operator-card {
	  	margin-top: 30px;
  		text-align: right;
	}
	.el-form-item{
 		margin: 30px 0 10px 30px;
	}
	img{
		width: 20px;
		height: 20px;
	}
</style>