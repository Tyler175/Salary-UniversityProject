<template>
  <div>
    <div class="cover" @click="close()"></div>

    <div class="absence-editor">
      <div>
        {{isCreate ? 'Добавить' : 'Изменить'}} информацию о пропуске
        <span class="material-icons close-icon" @click="close()">close</span>
      </div>

      <div class="row">
        <div class="column">
          <div class="row user-info">Период пропуска:</div>
          <div v-if="isUserAccountant" class="row user-info">Сотрудник:</div>
        </div>
        <div class="column">
          <div class="row" style="margin: 8px 0">
            <date-picker v-model="selectedDates"
                         @change="absence.startDate = selectedDates[0];
                                    absence.endDate = selectedDates[1];"
                         style="outline: 0;
                              width: 280px;
                              border: 1px solid #dce1e6;
                              border-radius: 3px;
                              padding: 6px 12px;"
                         type="date"
                         :range=true
                         range-separator = " - "
                         format="DD MMMM YYYY"
                         :lang="lang"
                         :clearable = "false"
                         input-class="date-picker kludge"
                         placeholder="Выбрать">
            </date-picker>
          </div>
          <div v-if="isUserAccountant" class="row" v-click-outside="deactivateWorkerFocus" style="margin: 8px 0; position: relative">
            <div style="position: relative; width: 100%">
              <input v-on:focus="workerFocus = true" type="text" class="border-input" style="width: 100%" v-model="selectedWorker" @input="getUsersByName()">
              <span class="material-icons close-icon" @click="selectedWorker=''; selectedWorkerId='';" style="position: absolute; top:calc(50% - 10px); right:18px;">close</span>
            </div>

            <div v-if="selectedWorker && workerFocus && workers.length" class="input-select">
              <div v-for="worker in workers" :key="worker.id" @click="selectedWorker=worker.firstName+worker.lastName+worker.patronymic; selectedWorkerId=worker.id; workerFocus=false;">
                {{worker.lastName}} {{worker.firstName}} {{worker.patronymic}}
              </div>
            </div>
          </div>

        </div>
      </div>
      <div class="row" style="align-items: flex-start">
        <div class="column">
          <div class="row user-info" style="margin: 0">Причина:</div>
        </div>
        <div class="column" style="width: 100%">
          <textarea v-model="absence.reason" class="border-input"></textarea>
        </div>
      </div>
      <div>
        <div class="btn btn-thin" @click="changeUser()" style="margin: 0">{{isCreate ? 'Добавить' : 'Изменить'}}</div> {{message}}
      </div>
    </div>
  </div>
</template>

<script>

import DatePicker from 'vue2-datepicker';
import 'vue2-datepicker/index.css';
import 'vue2-datepicker/locale/es/ru.js';
import UserService from "@/services/user.service";

export default {
  name: "absenceEditor",
  props: ['absence', 'userToEdit', 'success', 'close', 'isUserAccountant'],
  data() {
    return {
      message: '',

      selectedDates: [],

      selectedWorker: '',
      selectedWorkerId: '',
      workerFocus: false,
      workers:[],

      user:{},

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
      return !(this.absence && this.absence.id)
    },
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  methods:{
    deactivateWorkerFocus(){
      this.selectedWorker =
          this.workers.length && this.selectedWorker ? this.workers[0].lastName + ' ' + this.workers[0].firstName + ' ' + this.workers[0].patronymic
          : (this.user.id ? this.user.lastName + ' ' + this.user.firstName + ' ' + this.user.patronymic : '');
      this.selectedWorkerId = this.workers.length && this.selectedWorker ? this.workers[0].id : (this.user ? this.user.id : '');
      this.workerFocus = false;
    },
    changeUser(){

      if (!this.user.id){
        this.workers.find(i => i.id === this.selectedWorkerId).workerInfo.absences = [this.absence];
        this.user = this.isUserAccountant ? this.workers.find(i => i.id === this.selectedWorkerId) : this.currentUser;
      } else {
        this.user.workerInfo.absences  = [this.absence];
      }


      //you need validate form first
      //set id after save new one and change method to edit OR close window
      //show message after successful post/put
      //also you forgot about update list when you add new absence of edit existing
      UserService.changeUser(this.user.id, this.user).then(
          response => {

            this.success(response.data.id, response.data.workerInfo.absences.find(i => new Date(i.startDate).getTime() === new Date(this.absence.startDate).getTime()));
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },
    getUsersByName(){
      UserService.getUsersByName(this.selectedWorker).then(
          response => {
            this.workers = response.data.filter(i => i.workerInfo);
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

    if (this.userToEdit || !this.isUserAccountant){
      UserService.getUser(this.userToEdit ? this.userToEdit : this.currentUser.id).then(
          response => {
            this.user = response.data;
            if (this.absence.id){
              this.selectedDates = [new Date(this.absence.startDate),new Date(this.absence.endDate)];
              this.selectedWorker = this.user.lastName + ' ' + this.user.firstName + ' ' + this.user.patronymic;
              this.selectedWorkerId = this.user.id;
            }
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      )
    }


  }
}
</script>

<style scoped>

</style>