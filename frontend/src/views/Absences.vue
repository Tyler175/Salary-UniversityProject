<style scoped>
  .mx-datepicker{
    width: fit-content;
  }
  .user-info{
    width: fit-content;
    margin-right: 15px;
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

    <v-absenceEdit v-if="isEditorActive"
                   :absence="absenceToEdit"
                   :userToEdit = "userToEdit"
                   :success = "successEdit"
                   :close="closeEditor"
                   :isUserAccountant="isUserAccountant"></v-absenceEdit>

    <div class="container">
      <v-menu></v-menu>

      <div class="content" style="width: calc(80% - 15% - 285px + 245px);">
        <h1 class="h1">Пропуски работы</h1>
        <div class="separator"></div>
        <div class="row" style="align-items: flex-start">
          <div class="column">
            <div class="row user-info">Показать пропуски за период:</div>
            <div v-if="isUserAccountant" class="row user-info">Сотрудник:</div>
            <div v-if="isUserAccountant" class="row user-info">Должность:</div>
          </div>
          <div class="column">
            <div class="row" style="margin: 8px 0">
              <date-picker v-model="selectedDates"
                           @change="getAbsences"
                           style="outline: 0;
                              width: 280px;
                              border: 1px solid #dce1e6;
                              border-radius: 3px;
                              padding: 6px 12px;"
                           type="month"
                           :range=true
                           range-separator = " - "
                           format="MMMM YYYY"
                           :lang="lang"
                           :clearable = "false"
                           input-class="date-picker kludge"
                           placeholder="Выбрать">
              </date-picker>
            </div>
            <!-- it should be the component because you copy paste this fragment at least three times-->
            <div v-if="isUserAccountant" class="row" v-click-outside="deactivateWorkerFocus" style="margin: 8px 0; position: relative">
              <div style="position: relative; width: 100%">
                <input v-on:focus="workerFocus = true" type="text" class="border-input" style="width: 100%" v-model="selectedWorker" @input="getUsersByName()">
                <span class="material-icons close-icon" @click="selectedWorker=''; selectedWorkerId=''; getAbsences()" style="position: absolute; top:calc(50% - 10px); right:18px;">close</span>
              </div>

              <div v-if="selectedWorker && workerFocus && workers.length" class="input-select">
                <div v-for="worker in workers" :key="worker.id" @click="selectedWorker=worker.firstName+worker.lastName+worker.patronymic; selectedWorkerId=worker.id; workerFocus=false; getAbsences()">
                  {{worker.lastName}} {{worker.firstName}} {{worker.patronymic}}
                </div>
              </div>
            </div>
            <div v-if="isUserAccountant" class="row" v-click-outside="deactivatePositionFocus" style="margin: 8px 0; position: relative">
              <div style="position: relative; width: 100%">
                <input v-on:focus="positionFocus = true" type="text" class="border-input" style="width: 100%" v-model="selectedPosition">
                <span class="material-icons close-icon" @click="selectedPosition=''; selectedPositionId=''; getAbsences()" style="position: absolute; top:calc(50% - 10px); right:18px;">close</span>
              </div>

              <div v-if="positionFocus && positions.filter(i => i.name.toLowerCase().startsWith(selectedPosition.toLowerCase())).length" class="input-select">
                <div v-for="position in positions.filter(i => i.name.toLowerCase().startsWith(selectedPosition.toLowerCase()))" :key="position.id" @click="selectedPosition=position.name; selectedPositionId=position.id; positionFocus=false; getAbsences()">{{position.name}}</div>
              </div>
            </div>
          </div>
          <div class="row" style="margin: 10px 15px 10px auto"><div class="btn btn-thin" style="margin: 0" @click="openEditor({})">Добавить информацию о пропусках</div></div>
        </div>

        <div class="separator"></div>
        <div class="row">
          <div v-if="usersAbsences.length" style="width: 100%;">
            <div v-for="userAbsences in usersAbsences" :key="userAbsences.id">
              <div class="userAbsences" @click="openEditor(absence, userAbsences)"
                   v-bind:style="success === absence.id ? 'box-shadow: 0 2px 0 0 #a3dba8, 0 0 0 2px #b3eec1;': 'box-shadow: 0 1px 0 0 #dce1e6, 0 0 0 1px #e7e8ec;'"
                   :ref="'absence'+absence.id"
                   v-for="absence in userAbsences.workerInfo.absences" :key="absence.id">

                <img :src="userAbsences.profilePicture ?
                             require('../../public/images/store/'+userAbsences.profilePicture) :
                             require('../../public/images/default'+(userAbsences.gender ? 'Woman':'Man')+'.png')"
                     v-bind:alt="userAbsences.firstName"/>

                <div class="column" style="justify-content: space-between; width:250px">
                  <div style="margin-bottom: 12px; width: max-content">{{userAbsences.lastName}} {{userAbsences.firstName}} {{userAbsences.patronymic}}</div>
                  <div style="width: max-content">{{new Date(absence.startDate).toLocaleDateString()}} - {{new Date(absence.endDate).toLocaleDateString()}}</div>
                </div>
                <div style="margin-left: 30px">Причина:</div>
                <div style="margin-left: 10px; white-space: pre">{{absence.reason}}</div>
                <div style="margin-left: auto;">
                  <span v-on:click.stop="deleteAbsence(userAbsences.id,absence.id)" class="material-icons close-icon">close</span>
                </div>
              </div>

            </div>
          </div>
          <div style="padding-left: 15px" v-else>За данный период пропусков не было</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Menu from "@/views/components/Menu";
import Header from "@/views/components/Header";
import UserService from "@/services/user.service";
import absenceEditor from "@/views/components/absenceEditor";

import DatePicker from 'vue2-datepicker';
import 'vue2-datepicker/index.css';
import 'vue2-datepicker/locale/es/ru.js';


export default {
  name: "Absences",
  data(){
    return{
      message: '',
      test: '',

      selectedDates: [new Date(new Date()),new Date(new Date())],

      isEditorActive: false,
      absenceToEdit: {},
      userToEdit:{},
      success: 0,

      selectedWorker: '',
      selectedWorkerId: '',
      workerFocus: false,

      selectedPosition: '',
      selectedPositionId: '',
      positionFocus: false,

      workers:[],
      positions:[],
      usersAbsences: [],

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
    'v-absenceEdit': absenceEditor
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
    }
  },
  methods: {
    deactivateWorkerFocus(){
      this.selectedWorker = this.workers.length && this.selectedWorker ? this.workers[0].lastName + ' ' + this.workers[0].firstName + ' ' + this.workers[0].patronymic : '';
      this.selectedWorkerId = this.workers.length && this.selectedWorker ? this.workers[0].id : '';
      this.workerFocus = false;
    },
    deactivatePositionFocus(){
      this.selectedPosition = this.selectedPosition && this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase())) ? this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase())).name : ''
      this.selectedPositionId = this.selectedPosition && this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase())) ? this.positions.find(i => i.name.toLowerCase().startsWith(this.selectedPosition.toLowerCase())).id : ''
      this.positionFocus = false;
    },

    openEditor(absence, user){
      this.absenceToEdit = absence;
      this.userToEdit = user ? user.id : '';
      this.isEditorActive = true;
    },
    closeEditor(){
      this.isEditorActive = false;
    },
    async scrollToAbsence(number_slide){
      let slide = this.$refs['absence'+number_slide][0]

      let count = 100;
      let top = window.scrollY + slide.getBoundingClientRect().y;
      let start_top = window.scrollY;

      let delta = (top - start_top) / count;
      for (let i=0;i<count; i++) {
        await new Promise(resolve => {
          window.setTimeout(function () {
            resolve()
          }, 1000 / count)
        })
        window.scrollTo(0, start_top + i*delta)
      }
    },
    successEdit(userId, absence){
      this.closeEditor();
      let changedAbsences = this.usersAbsences.find(i => i.id === userId).workerInfo.absences;
      if (!changedAbsences.find(i => i.id === absence.id)) changedAbsences.push(absence);


      this.success = absence.id;
      setTimeout(()=>this.success=0,1500)
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
    },

    getAbsences(){
      UserService.getAbsences(this.isUserAccountant ? this.selectedWorkerId : this.currentUser.id, this.selectedPositionId, this.selectedDates[0], this.selectedDates[1]).then(
          response => {
            this.usersAbsences = response.data;
            this.usersAbsences.forEach(i => i.workerInfo.absences.sort((a1, a2) => new Date(a1.startDate).getTime() - new Date(a2.startDate).getTime()))
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },
    deleteAbsence(userId, absenceId){
      UserService.deleteAbsence(absenceId).then(
          response => {
            response.data;
            let index = this.usersAbsences.find(i => i.id === userId).workerInfo.absences.findIndex(i => i.id === absenceId);
            this.usersAbsences.find(i => i.id === userId).workerInfo.absences.splice(index, 1);

          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      )
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
    this.getAbsences();
  },
  updated(){
    if (this.success){
      this.scrollToAbsence(this.success);
    }
  }
}
</script>