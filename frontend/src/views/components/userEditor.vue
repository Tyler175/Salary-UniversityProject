<style scoped>
  input{
    min-width: 300px;
  }
  .alert-error{
    margin-left: 15px;
  }
</style>
<template>
  <div>
    <div class="cover" @click="close()"></div>

    <form class="absence-editor" @submit.prevent="changeUser">
      <div>
        {{isCreate ? 'Добавить пользователя' : 'Изменить информацию о пользователе'}}
        <span class="material-icons close-icon" @click="close()">close</span>
      </div>
      <div class="row">
        <div class="column">
          <div class="row user-info">Фамилия пользователя:</div>
          <div class="row user-info">Имя пользователя:</div>
          <div class="row user-info">Отчество пользователя:</div>
        </div>
        <div class="column">
          <div class="row" style="margin: 8px 0">
            <input class="border-input" v-model="user.lastName" v-validate="'required|max:30'" name="фамилия"/>
          </div>
          <div class="row" style="margin: 8px 0">
            <input class="border-input" v-model="user.firstName" v-validate="'required|max:30'" name="имя"/>
          </div>
          <div class="row" style="margin: 8px 0">
            <input class="border-input" v-model="user.patronymic" v-validate="'max:30'" name="отчество"/>
          </div>

        </div>
      </div>


      <div
          v-if="errors.has('фамилия')"
          class="row alert-error"
      >{{errors.first('фамилия')}}</div>
      <div
          v-if="errors.has('имя')"
          class="row alert-error"
      >{{errors.first('имя')}}</div>



      <div class="separator"></div>

      <div class="row">
        <div class="column">
          <div class="row user-info">Email пользователя:</div>
          <div class="row user-info">День рождения:</div>
          <div class="row user-info">Пол:</div>
        </div>
        <div class="column">
          <div class="row"  style="margin: 8px 0;">
            <input
                v-model="user.email"
                v-validate="'required|email|max:50'"
                type="email"
                class="border-input"
                name="email"
            />
          </div>
          <div class="row">
            <date-picker v-model="user.birthday"
                         style="outline: 0;
                              width: 274px;
                              border: 1px solid #dce1e6;
                              border-radius: 3px;
                              padding: 6px 12px;"
                         type="date"
                         name = "день рождения"
                         v-validate="'required'"
                         format="DD MMMM YYYY г." :lang="lang"
                         :clearable = "false"
                         :confirm="true"
                         input-class="date-picker"
                         placeholder="Выбрать">
            </date-picker>
          </div>
          <div class="row" style="margin: calc(15px - 5px) 0; text-align: center;">
              <span v-bind:style="[user.gender ? {'background': 'none'} : {'background': 'rgb(223, 230, 237)'}]"
                    @click="user.gender=false;"
                    class="material-icons square-border-button">
                man
              </span>
            <div style="width: 100px;">{{user.gender ? 'Женский' : 'Мужской'}}</div>
            <span v-bind:style="[user.gender ? {'background': 'rgb(223, 230, 237)'} : {'background': 'none'}]"
                  @click="user.gender=true;"
                  class="material-icons square-border-button">
                woman
              </span>
          </div>
        </div>
      </div>
      <div
          v-if="errors.has('email')"
          class="row alert-error"
      >{{errors.first('email')}}</div>

      <div
          v-if="errors.has('день рождения')"
          class="row alert-error"
      >{{errors.first('день рождения')}}</div>

      <div class="separator"></div>
      <div class="row" v-if="user.roles">
        <div class="user-info">Роль:</div>
        <div class="row" style="margin: calc(15px - 5px) 0; text-align: center;">
              <span v-bind:style="[user.roles.find(i => i.name==='ROLE_ACCOUNTANT') ? {'background': 'rgb(223, 230, 237)'} : {'background': 'none'}]"
                    @click="user.roles=[{id: 1, name: 'ROLE_USER'}, {id: 2, name: 'ROLE_ACCOUNTANT'}];"

                    class="material-icons square-border-button">
                account_balance
              </span>
          <div style="width: 100px;">{{user.roles.find(i => i.name==='ROLE_ACCOUNTANT') ? 'Бухгалтер' : 'Сотрудник'}}</div>
          <span v-bind:style="[user.roles.find(i => i.name==='ROLE_ACCOUNTANT') ? {'background': 'none'} : {'background': 'rgb(223, 230, 237)'}]"
                @click="user.roles=[{id: 1, name: 'ROLE_USER'}];"
                class="material-icons square-border-button">
                school
              </span>
        </div>
      </div>
      <div v-if="user.workerInfo && !user.roles.find(i => i.name==='ROLE_ACCOUNTANT')">
        <div class="row">
          <div class="user-info">Должность:</div>
          <div class="row"  v-click-outside="deactivatePositionFocus" style="margin: 8px 0; position: relative">
            <div style="position: relative; width: 100%">
              <input v-on:focus="positionFocus = true" type="text" class="border-input" style="width: 100%" v-model="selectedPosition">
              <span class="material-icons close-icon" @click="selectedPosition=''; selectedPositionId='';" style="position: absolute; top:calc(50% - 10px); right:18px;">close</span>
            </div>

            <div v-if="positionFocus && positions.length && positions.filter(i => i.name.toLowerCase().startsWith(selectedPosition.toLowerCase())).length" class="input-select">
              <div v-for="position in positions.filter(i => i.name.toLowerCase().startsWith(selectedPosition.toLowerCase()))" :key="position.id" @click="selectedPosition=position.name; selectedPositionId=position.id; positionFocus=false;">{{position.name}}</div>
            </div>
          </div>
        </div>
        <div class="row">
          <div class="user-info">Семейное положение:</div>
          <div class="row">
            <div v-click-outside="deactivateSelectFocus" style="position: relative; width: fit-content; min-width: 6em; padding-right: 20px">
              <div @click="selectFocus=!selectFocus" style="display: flex; justify-content: space-between;">
                <div style="margin-right: 5px">{{maritalStatus[user.workerInfo ? (user.workerInfo.maritalStatus % 3):0]}}</div>
                <span class="material-icons" v-bind:style="{ 'padding-right': '0px', transform: 'rotate('+ (selectFocus ? '90':'0')+'deg)'}">chevron_right</span>
              </div>
              <div class="select" v-bind:style="[selectFocus ? {display:'block'} : {display: 'none'}]">
                <div @click="selectFocus=!selectFocus">Placeholder</div>
                <div v-for="(status, index) in maritalStatus" :key="index"
                     @click="()=>{user.workerInfo.maritalStatus=index; deactivateSelectFocus();}">
                  {{status}}
                </div>
              </div>

            </div>
          </div>
        </div>
        <div class="row">
          <div class="user-info">Количество детей:</div>
          <div class="row" style="padding-right: 20px">
            <div><span
                style="display: block; background-color: rgb(223, 230, 237); border-radius: 3px; padding: 0; cursor: pointer"
                @click="()=>{if(user.workerInfo.children > 0) {user.workerInfo.children-=1;}}"
                class="material-icons unselectable">remove</span>
            </div>
            <div style="padding: 0 10px; width: 100%; text-align: center">{{user.workerInfo.children}}</div>
            <div><span
                style="display: block; background-color: rgb(223, 230, 237); border-radius: 3px; padding: 0; cursor: pointer"
                @click="()=>{if(user.workerInfo.children < 10) {user.workerInfo.children+=1;}}"
                class="material-icons unselectable">add</span>
            </div>
          </div>
        </div>
      </div>

      <div v-if="isCreate">
        <div class="separator"></div>

        <div class="row">
          <div class="user-info">Новый пароль:</div>
          <input type="password"
                 class="border-input"
                 v-model="newPassword" name="пароль"
                 v-validate="'required|min:6|max:40'"/>
        </div>
        <div class="alert-error" v-if="this.newPassword.includes(' ') && passwordFocus">Пароль не может содержать пробелы</div>
        <div
            v-if="errors.has('пароль')"
            class="alert-error"
        >{{errors.first('пароль')}}</div>
        <div
            v-else-if="errors.has('пароль')"
            class="alert-error">
          {{errors.first('пароль')}}
        </div>
        <div class="row">
          <div class="user-info">Повторите новый пароль:</div>
          <input type="password"
                 class="border-input"
                 v-model="checkedPassword"/>
        </div>
        <div v-if="notSamePasswords" class="alert-error">Пароли не совпадают</div>

      </div>



      <div>
        <button class="btn btn-thin" style="margin: 0">{{isCreate ? 'Добавить пользователя' : 'Изменить'}}</button> {{message}}
      </div>
    </form>
  </div>
</template>

<script>

import DatePicker from 'vue2-datepicker';
import 'vue2-datepicker/index.css';
import 'vue2-datepicker/locale/es/ru.js';
import UserService from "@/services/user.service";

export default {
  name: "userEditor",
  props: ['userToEdit', 'success', 'close'],
  data() {
    return {
      message: '',

      newPassword: '',
      checkedPassword: '',
      successful: false,

      selectFocus: false,
      positionFocus: false,

      selectedPosition: '',

      positions:[],

      user:{
        id:'',
        email: '',
        password: '',
        firstName: '',
        lastName: '',
        patronymic: '',

        birthday: '',
        gender: false,
        profilePicture: '',

        workerInfo: {
          position: {},
          maritalStatus: 0,
          children: 0,
        },

        roles: [
          {
            id: 1,
            name: 'ROLE_USER'
          }
        ]
      },

      lang: {
        formatLocale: {
          months: ['января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря'],
        }
      }
    };
  },
  components:{
    DatePicker
  },
  computed:{
    isCreate(){
      return !(this.userToEdit)
    },

    maritalStatus(){
      return [
        this.user.gender ? 'Не замужем': 'Не женат',
        this.user.gender ? 'Замужем': 'Женат',
        this.user.gender ? 'Разведена': 'Разведен'
      ]
    },


    notSamePasswords () {
      if (this.newPassword !== '') {
        return (this.newPassword !== this.checkedPassword)
      } else {
        return false
      }
    },
  },
  methods:{
    deactivateSelectFocus(){
      this.selectFocus=false;
    },
    deactivatePositionFocus(){

      this.user.workerInfo.position =
          this.selectedPosition && this.positions.length && this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase()))
              ? this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase()))
              : {};
      this.positionFocus = false;
    },

    changeUser(){

      if (this.isCreate){
        this.user.password = this.newPassword;

        if (this.user.roles.find(i => i.name==='ROLE_ACCOUNTANT')){
          this.user.workerInfo = null;
        }

        this.$validator.validate().then(isValid => {
          if (isValid && !this.notSamePasswords) {
            this.$store.dispatch('auth/register', this.user).then(
                data => {
                  this.success(data);
                },
                error => {
                  this.message =
                      (error.response && error.response.data && error.response.data.message) ||
                      error.message ||
                      error.toString();
                }
            );
          }
        });
      } else {
        UserService.changeUser(this.user.id, this.user).then(
            response => {
              this.success(response.data);
            },
            error => {
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );
      }

    }
  },
  mounted() {
    if (!this.isCreate){
      UserService.getUser(this.userToEdit).then(
          response => {
            this.user = response.data;
            this.user.birthday = new Date(this.user.birthday);
            this.selectedPosition = this.user.workerInfo.position.name;
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );

    }
    UserService.getPositions().then(
        response => {
          this.positions = response.data;
        },
        error => {
          this.message =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();
        }
    );
  }
}
</script>

