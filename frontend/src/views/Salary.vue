<style scoped>
  .mx-datepicker{
    width: fit-content;
  }
  input:disabled{
    background: none;
  }
</style>
<template>
  <div v-if="currentUser">
    <v-header
        v-bind:user="{
        firstName: user.firstName,
        profilePicture: user.profilePicture,
        gender: user.gender
      }">
    </v-header>

    <v-message v-if="showMessage" :message="message" :close="close"></v-message>

    <div class="container">
      <v-menu></v-menu>

      <div class="content" style="width: calc(80% - 15% - 285px + 245px)">
        <h1 class="h1">Распределение зарплат</h1>
        <div class="separator"></div>
        <div class="row" style="align-items: flex-start;">

          <div class="row user-info">Месяц:</div>
          <div class="row" style="margin: 8px 0">
            <date-picker v-model="selectedDate"
                         @change="getSalary"
                         style="outline: 0;
                              width: 200px;
                              border: 1px solid #dce1e6;
                              border-radius: 3px;
                              padding: 6px 12px;"
                         type="month"
                         format="MMMM YYYY"
                         :lang="lang"
                         :clearable = "false"
                         input-class="date-picker"
                         placeholder="Выбрать">
            </date-picker>
          </div>
          <div v-if="isUserAccountant" class="column" style="margin-left: 50px">
            <div class="row">
              <div class="user-info">Премиальный бюджет:</div> <span>{{premiumBudget}}</span>
            </div>
            <div class="row">
              <div class="user-info">Остаток:</div> <span>{{remainPremiumBudget}}</span>
            </div>


          </div>
          <div v-else class="column" style="margin-left: 50px">
            <div class="row">
              <div class="user-info">Плановые дни:</div> <span>21</span>
            </div>
            <div class="row">
              <div class="user-info">Фактические дни:</div> <span>17</span>
            </div>
          </div>
        </div>

        <div class="separator"></div>

        <table class="table">
          <thead class="thead">
          <tr class="tr">
            <th class="th" v-for="(key, index) in gridLocaleColumns"
                :key="index"
                @click="sortBy(index)"
                :class="{ active: sortKey == gridColumns[index] }">
              {{ key | capitalize }}
              <span v-if="isUserAccountant" class="arrow" :class="sortOrders[gridColumns[index]] > 0 ? 'asc' : 'dsc'">
              </span>
            </th>
          </tr>
          </thead>
          <tbody class="tbody">
          <tr class="tr" v-for="(entry, entryIndex) in filteredHeroes"
            :key = "entryIndex">
            <td class="td" v-for="(key, index) in gridColumns" :key = "index">
              <div v-if="key!=='award'">{{isFloat(entry[key]) ? (entry[key]).toFixed(2) : (entry[key])}}</div>
              <input v-else :disabled="!isUserAccountant" class="table-input" type="number" v-model="entry[key].amount" @input="changeSalary(entry)">
            </td>
          </tr>
          </tbody>
        </table>





      </div>
    </div>
  </div>
</template>

<script>
import Menu from "@/views/components/Menu";
import Header from "@/views/components/Header";
import UserService from "@/services/user.service";
import message from "@/views/components/message";

import DatePicker from 'vue2-datepicker';
import 'vue2-datepicker/index.css';
import 'vue2-datepicker/locale/es/ru.js';


export default {
  name: "Salary",
  data(){

    return{
      message: '',

      showMessage: false,

      sortKey: "",
      sortOrders: {},
      searchQuery: "",
      gridColumns: ["name", "position", "absenceDays", "salary", "award", "result"],
      gridLocaleColumns: ["ФИО", "Должность", "Пропуски", "Зарплата", "Премия", "Итого"],

      gridData: [],
      gridData1: [
        { name: "Chuck Norris", power: Infinity },
        { name: "Bruce Lee", power: 9000 },
        { name: "Jackie Chan", power: 7000 },
        { name: "Jet Li", power: 8000 }
      ],

      selectedDate: new Date(new Date()),

      premiumBudget: 0,

      users: [],

      user:{
        firstName: '',
        profilePicture: '',
        gender: false
      },

      lang: {
        formatLocale: {
          months: ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'],
        }
      }
    };
  },
  components:{
    DatePicker,
    'v-menu' : Menu,
    'v-header': Header,
    'v-message': message
  },
  computed: {
    remainPremiumBudget(){
      let res = this.premiumBudget
      this.gridData.forEach(i => res-=i.award.amount);
      return res;
    },

    filteredHeroes: function() {
      var sortKey = this.sortKey;
      var filterKey = this.searchQuery && this.searchQuery.toLowerCase();
      var order = this.sortOrders[sortKey] || 1;
      var heroes = this.gridData;
      if (filterKey) {
        heroes = heroes.filter(function(row) {
          return Object.keys(row).some(function(key) {
            return (
                String(row[key])
                    .toLowerCase()
                    .indexOf(filterKey) > -1
            );
          });
        });
      }
      if (sortKey) {
        heroes = heroes.slice().sort(function(a, b) {
          a = a[sortKey];
          b = b[sortKey];
          return (a === b ? 0 : a > b ? 1 : -1) * order;
        });
      }
      return heroes;
    },

    currentUser() {
      return this.$store.state.auth.user;
    },
    isUserAdmin() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_ADMIN');
      }

      return false;
    },
    isUserAccountant() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_ACCOUNTANT');
      }

      return false;
    }
  },
  filters: {
    capitalize: function(str) {
      return str.charAt(0).toUpperCase() + str.slice(1);
    }
  },
  methods: {
    close(){
      this.showMessage = false;
      this.message = ''
    },
    changeSalary(user){
      if (this.remainPremiumBudget < 0){
        user.award.amount = 0;
        this.message = "Вы вышли за пределы премиального бюджета"
        this.showMessage = true;
      }
      user.result = ((parseFloat(user.salary) + parseFloat(user.award.amount))*0.87).toFixed(2);
      user.award.month = this.selectedDate;
      UserService.changeAwards(user).then(
          response => {
            user.award = response.data;
          },
          error=>{
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      )

    },
    isFloat(n){
      return Number(n) === n && n % 1 !== 0;
    },
    sortBy: function(index) {
      let key = this.gridColumns[index];
      this.sortKey = key;
      this.sortOrders[key] = this.sortOrders[key] * -1;
    },
    changeUser(){
      UserService.changeUser(this.currentUser.id, this.user).then(
          response => {
            response.data;
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },


    getSalary(){
      UserService.getSalary(this.selectedDate).then(
          response => {
            this.gridData = this.isUserAccountant ? response.data : response.data.filter(i => i.id === this.currentUser.id);

            this.gridData.forEach(i => i.result = (i.salary + i.award.amount)*0.87)
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
      UserService.getBudget(this.selectedDate).then(
          response => {
            this.premiumBudget = response.data.amount;
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }

    let sortOrders = {};
    this.gridColumns.forEach(function(key) {
      sortOrders[key] = 1;
    });
    this.sortOrders = sortOrders;

    UserService.getUser(this.currentUser.id).then(
        response => {
          this.user = response.data;
          this.user.birthday = new Date(response.data.birthday);
        },
        error => {
          this.message =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();
        }
    );
    this.getSalary();
  }
}
</script>