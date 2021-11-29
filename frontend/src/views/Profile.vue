<style scoped>
.mx-datepicker{
  width: fit-content;
}
.alert-error{
  text-align: left;
  padding-left: 15px;
}
</style>
<template>
  <div v-if="currentUser"><v-header
      v-bind:user="{
        firstName: user.firstName,
        profilePicture: user.profilePicture,
        gender: user.gender
      }"></v-header>
  <div class="container">


    <v-menu></v-menu>

    <div class="content" style="max-width: 200px;">
      <div style="position: relative">
        <input
            class="image-picker"
            type="file"
            accept="image/*"
            @change="onImageSelected"
        />
        <img class="profile-picture"
             :src="user.profilePicture ?
           require('../../public/images/store/'+user.profilePicture) :
           require('../../public/images/default'+(user.gender ? 'Woman':'Man')+'.png')" alt="profile picture"/>
      </div>


      <div class="row" style="background-color: #dfe6ed; margin-bottom: 0; padding: 5px; border-radius: 3px; justify-content: center;">
        {{hasRole==='Сотрудник' ? this.user.workerInfo.position.name : hasRole}}
      </div>
      <div v-if="message" class="row" style="margin-bottom: 0; word-break:break-all ;color: #ff6464; justify-content: center">{{message}}</div>
    </div>
    <div class="content" style="width: calc(80% - 15% - 285px)">
      <div>
        <div class="row" style="margin: 0 15px">
          <input class="username" type="text" maxlength="30" v-autowidth="{maxWidth: '700px', minWidth: '20px', comfortZone: 0}" v-model="user.lastName" @change="changeUser">
          <input class="username" type="text" maxlength="30" v-autowidth="{maxWidth: '700px', minWidth: '20px', comfortZone: 0}" v-model="user.firstName" @change="changeUser">
          <input class="username" type="text" maxlength="30" v-autowidth="{maxWidth: '700px', minWidth: '20px', comfortZone: 0}" v-model="user.patronymic" @change="changeUser">
        </div>
        <div class="separator"></div>
        <div class="row">
          <div class="column">
            <div class="row user-info">Почта:</div>
            <div class="row user-info">День рождения:</div>
            <div class="row user-info">Пол:</div>
          </div>
          <div class="column">
            <div class="row">{{user.email}}</div>
            <div class="row">
              <date-picker v-model="user.birthday"
                           @change="changeUser"
                         type="date"
                         format="DD MMMM YYYY г." :lang="lang"
                         :clearable = "false"
                         :confirm="true"
                         input-class="date-picker"
                         placeholder="Выбрать">
              </date-picker>
            </div>
            <div class="row" style="margin: calc(15px - 5px) 0; text-align: center;">
              <span v-bind:style="[user.gender ? {'background': 'none'} : {'background': 'rgb(223, 230, 237)'}]"
                    @click="user.gender=false; changeUser()"
                    class="material-icons square-border-button">
                man
              </span>
              <div style="width: 100%;">{{user.gender ? 'Женский' : 'Мужской'}}</div>
              <span v-bind:style="[user.gender ? {'background': 'rgb(223, 230, 237)'} : {'background': 'none'}]"
                    @click="user.gender=true; changeUser()"
                    class="material-icons square-border-button">
                woman
              </span>
            </div>
          </div>
        </div>
        <div class="separator"></div>
        <div v-if="user.workerInfo">
          <div class="row">
            <div class="column">
              <div class="row user-info">Семейное положение:</div>
              <div class="row user-info">Количество детей:</div>
            </div>
            <div class="column">
              <div class="row">
                <div v-click-outside="deactivateSelectFocus" style="position: relative; width: fit-content; min-width: 6em; padding-right: 20px">
                  <div @click="selectFocus=!selectFocus" style="display: flex; justify-content: space-between;">
                    <div style="margin-right: 5px">{{maritalStatus[user.workerInfo ? (user.workerInfo.maritalStatus % 3):0]}}</div>
                    <span class="material-icons" v-bind:style="{ 'padding-right': '0px', transform: 'rotate('+ (selectFocus ? '90':'0')+'deg)'}">chevron_right</span>
                  </div>
                  <div class="select" v-bind:style="[selectFocus ? {display:'block'} : {display: 'none'}]">
                    <div @click="selectFocus=!selectFocus">Placeholder</div>
                    <div v-for="(status, index) in maritalStatus" :key="index"
                         @click="()=>{user.workerInfo.maritalStatus=index; deactivateSelectFocus(); changeUser()}">
                      {{status}}
                    </div>
                  </div>

                </div>
              </div>
              <div class="row" style="padding-right: 20px">
                <div><span
                    style="display: block; background-color: rgb(223, 230, 237); border-radius: 3px; padding: 0; cursor: pointer"
                    @click="()=>{if(user.workerInfo.children > 0) {user.workerInfo.children-=1; changeUser()}}"
                    class="material-icons unselectable">remove</span>
                </div>
                <div style="padding: 0 10px; width: 100%; text-align: center">{{user.workerInfo.children}}</div>
                <div><span
                    style="display: block; background-color: rgb(223, 230, 237); border-radius: 3px; padding: 0; cursor: pointer"
                    @click="()=>{if(user.workerInfo.children < 10) {user.workerInfo.children+=1; changeUser()}}"
                    class="material-icons unselectable">add</span>
                </div>
              </div>
            </div>
          </div>
          <div class="separator"></div>
        </div>
        <div v-click-outside="deactivatePasswordFocus" v-bind:class="[passwordFocus ? 'active-block' : '', 'column']">

          <div v-if="response && passwordFocus" class="alert-error">{{response}}</div>
          <div class="row">
            <div class="user-info">Старый пароль:</div>
            <input type="password"
                   class="border-input"
                   v-on:focus="passwordFocus = true"
                   v-model="oldPassword"/>
          </div>
          <div class="row">
            <div class="user-info">Новый пароль:</div>
            <input type="password"
                   class="border-input"
                   v-on:focus="passwordFocus = true"
                   v-model="newPassword" name="пароль"
                   v-validate="'required|min:6|max:40'"/>
          </div>
          <div class="alert-error" v-if="this.newPassword.includes(' ') && passwordFocus">Пароль не может содержать пробелы</div>
          <div
              v-else-if="errors.has('пароль') && passwordFocus"
              class="alert-error">
            {{errors.first('пароль')}}
          </div>
          <div class="row">
            <div class="user-info">Повторите новый пароль:</div>
            <input type="password"
                   class="border-input"
                   v-on:focus="passwordFocus = true"
                   v-model="checkedPassword"/>
          </div>
          <div v-if="notSamePasswords && passwordFocus" class="alert-error">Пароли не совпадают</div>
          <div class="row">
            <div class="user-info"></div>
            <div class="btn btn-thin" style="margin: 0" @click="changePassword">Сменить пароль</div>
          </div>

        </div>
        <div class="separator"></div>
      </div>
      <div class="row">
        <button class="btn" style="margin: 15px" @click.prevent="logOut">
          Выйти
        </button>
      </div>

        <!-- END -->

      </div>
  </div>
  </div>
</template>

<script>
import UserService from '../services/user.service';
import Menu from "@/views/components/Menu";
import Header from "@/views/components/Header";

import DatePicker from 'vue2-datepicker';
import 'vue2-datepicker/index.css';
import 'vue2-datepicker/locale/es/ru.js';

export default {
  name: 'Profile',
  data(){
    return{
      message: '',
      user:{
        id:'',
        email:'',
        firstName:'',
        lastName:'',
        patronymic: '',
        birthday: new Date(),
        gender: false,
        profilePicture: '',

        workerInfo:{
          maritalStatus: 0,
          children: 0
        }
      },

      selectedProfilePicture: null,

      selectFocus: false,
      passwordFocus: false,

      oldPassword: '',
      newPassword: '',
      checkedPassword: '',
      response: '',

      lang: {
        formatLocale: {
          months: ['января', 'февраля', 'марта', 'апреля', 'мая', 'июня', 'июля', 'августа', 'сентября', 'октября', 'ноября', 'декабря'],
        }
      },
    };
  },
  components:{
    DatePicker,
    'v-menu' : Menu,
    'v-header': Header
  },
  computed: {
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
    },
    hasRole(){
      if (this.currentUser.roles.includes('ROLE_ADMIN')) return 'Администратор';
      if (this.currentUser.roles.includes('ROLE_ACCOUNTANT')) return 'Бухгалтер';
      if (this.currentUser.roles.includes('ROLE_USER')) return 'Сотрудник';
      return 0;

    },
    maritalStatus(){
      return [
        this.user.gender ? 'Не замужем': 'Не женат',
        this.user.gender ? 'Замужем': 'Женат',
        this.user.gender ? 'Разведена': 'Разведен'
      ]
    },


    notSamePasswords () {
      if (this.newPassword !== '' && this.checkedPassword !== '') {
        return (this.newPassword !== this.checkedPassword)
      } else {
        return false
      }
    },
  },

  methods: {
    onImageSelected(event) {
      let formData = new FormData();
      let file = event.target.files[0];
      formData.append("profilePicture", file, file.name);
      UserService.uploadProfilePicture(this.currentUser.id, formData).then(
          response => {
            this.user.profilePicture = response.data.profilePicture;
          },
          error => {
            // eslint-disable-next-line no-console
            console.log(error);
          }
      );
    },

    deactivatePasswordFocus(){
      this.passwordFocus=false;
    },
    deactivateSelectFocus(){
      this.selectFocus=false;
    },
    changePassword(){
      this.passwordFocus = true;
      if (!this.notSamePasswords && this.newPassword !== '' && this.checkedPassword !== ''){
        this.$validator.validate().then(isValid => {
          if (isValid && !this.newPassword.includes(' ')) {
            UserService.changePassword(this.currentUser.id,this.oldPassword,this.newPassword).then(
                response => {
                  if (response.data){
                    this.response = 'Пароль был успешно изменен';
                  } else {
                    this.response = "Неверный старый пароль пароль";
                  }
                }
            );
          }
        });
      } else this.response = 'Заполните все поля'
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
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
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


  }
};
</script>