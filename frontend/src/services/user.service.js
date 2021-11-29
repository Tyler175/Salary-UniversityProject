import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/';

class UserService {
  getAllUsers(){
    return axios.get(API_URL + 'users', {headers: authHeader()});
  }
  getUser(user_id) {
    return axios.get(API_URL + 'users/' + user_id, { headers: authHeader() });
  }
  getUsersByName(name){
    return axios.get(API_URL + 'users', {headers: authHeader(), params:{name: name}});
  }
  uploadProfilePicture(user_id, profilePicture){
    let header = {
      "Content-Type": "multipart/form-data"
    };
    header.Authorization = authHeader().Authorization;
    return axios.post(API_URL + 'users/upload/' + user_id, profilePicture, {headers: header});
  }
  changePassword(user_id, oldPassword, newPassword){
    return axios.put(API_URL + 'users/' + user_id + '/' + oldPassword, {"password" : newPassword}, { headers: authHeader() });
  }
  changeUser(userId, user){
    user.password = 'password';
    return axios.put(API_URL + 'users/' + userId, user, { headers: authHeader() });
  }
  deleteUser(userId){
    return axios.delete(API_URL + 'users/' + userId, {headers: authHeader()});
  }


  getAbsences(userId, position, startDate, endDate){
    return axios.get(API_URL + 'users/absences', {headers: authHeader(), params:{userId: userId, position: position, startDate: startDate, endDate: endDate}});
  }
  deleteAbsence(absenceId){
    return axios.delete(API_URL + 'users/absences/'+absenceId, {headers: authHeader()});
  }

  getPositions(){
    return axios.get(API_URL + 'positions', { headers: authHeader() });
  }

  getSalary(month){
    return axios.get(API_URL + 'salary', {headers: authHeader(), params:{month: month}});
  }
  getBudget(month){
    return axios.get(API_URL + 'salary/budget', {headers: authHeader(), params:{month: month}});
  }
  changeAwards(user){
    // eslint-disable-next-line no-console
    console.log(user);
    return axios.put(API_URL + 'salary/awards', user,{headers: authHeader()})
  }

}

export default new UserService();
