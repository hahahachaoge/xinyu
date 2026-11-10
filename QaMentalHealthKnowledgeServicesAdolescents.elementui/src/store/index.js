
import { defineStore } from "pinia";
import { post } from '@/api/http';
//  状态管理
export const useCommonStore = defineStore("common", {
  state: () => ({
    // 初始状态
    token: null,
    userInfo: null,
    roleType: undefined,
    hasUserInfo: false,
  }),
  getters: {
    userId: (state) => state.userInfo && state.userInfo.id,
  },
  actions: {
    async login(userInfoData) {
      let res = await post("/sysUser/signIn", userInfoData);

      if (res.success) {
        this.$patch({ token: res.data });
      }
 
      return res;
    },
    async getInfo() {
      let res = await post("/sysUser/getByToken", {});

      this.$patch({
        userInfo: res.data,
        hasUserInfo: res.data != null,
        roleType: res.data.roleTypeFormat,
      });
      return res;
    },
    async logout() {
      this.$patch({
        token: null,
        userInfo: null,
        roleType: null,
        hasUserInfo: false,
      });
    },
    resetState() {
      this.$patch({
        token: null,
        userInfo: null,
        roleType: undefined,
        hasUserInfo: false,
      });
    },
  },

  persist: {
    key: "common-store",
    storage: localStorage,
    paths: ["token"],
  },
});
