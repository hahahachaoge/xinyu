import { useCommonStore } from "@/store";
import AdminLayout from "@/views/Admin/Layout/index.vue";
import FrontLayout from "@/views/Front/Layout/index.vue";
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import { createRouter, createWebHashHistory } from "vue-router";

// 直接导入组件
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import ForgetPassword from "../views/ForgetPassword.vue";
import NotFound from "../views/NotFound.vue";
import FrontHome from "../views/Front/Home.vue";
import FrontAiChat from "../views/Front/AiChat.vue";
import FrontArticleList from "../views/Front/ArticleList.vue";
import FrontArticleDetail from "../views/Front/ArticleDetail.vue";
import FrontScaleList from "../views/Front/ScaleList.vue";
const FrontScaleTest = () => import("../views/Front/ScaleTest.vue");
import FrontAssessmentDetail from "../views/Front/AssessmentDetail.vue";
import FrontNoticeDetail from "../views/Front/NoticeDetail.vue";
import AdminHome from "../views/Admin/Home.vue";
import UserPerson from "../views/Admin/UserPerson.vue";
import PasswordEdit from "../views/Admin/PasswordEdit.vue";
import UserList from "../views/Admin/UserList.vue";
import AdminArticleList from "../views/Admin/ArticleList.vue";
import AdminArticleCommentList from "../views/Admin/ArticleCommentList.vue";
import AdminArticleTypeList from "../views/Admin/ArticleTypeList.vue";
import AdminAssessmentRecordList from "../views/Admin/AssessmentRecordList.vue";
import AdminBannerList from "../views/Admin/BannerList.vue";
import AdminChatMessageList from "../views/Admin/ChatMessageList.vue";
import AdminChatSessionList from "../views/Admin/ChatSessionList.vue";
import AdminDocumentChunkList from "../views/Admin/DocumentChunkList.vue";
import AdminFavoriteList from "../views/Admin/FavoriteList.vue";
import AdminHotQuestionList from "../views/Admin/HotQuestionList.vue";
import AdminKnowledgeDocumentList from "../views/Admin/KnowledgeDocumentList.vue";
import AdminKnowledgeTypeList from "../views/Admin/KnowledgeTypeList.vue";
import AdminNoticeList from "../views/Admin/NoticeList.vue";
import AdminNoticeTypeList from "../views/Admin/NoticeTypeList.vue";
import AdminQaFeedbackList from "../views/Admin/QaFeedbackList.vue";
import AdminScaleInfoList from "../views/Admin/ScaleInfoList.vue";
import AdminScaleInfoEdit from "../views/Admin/ScaleInfoEdit.vue";
import AdminScaleQuestionList from "../views/Admin/ScaleQuestionList.vue";
import AdminScoreRuleList from "../views/Admin/ScoreRuleList.vue";
import AdminUserStatistics from "../views/Admin/UserStatistics.vue";
import AdminQaStatistics from "../views/Admin/QaStatistics.vue";
import AdminKnowledgeStatistics from "../views/Admin/KnowledgeStatistics.vue";
import AdminAssessmentStatistics from "../views/Admin/AssessmentStatistics.vue";

// 配置 NProgress
NProgress.configure({
  showSpinner: false, // 不显示加载圈
  speed: 1000,
  trickle: false,
});

// 路由配置
const routes = [
  {
    path: "/",
    redirect: "/front",
  },
  {
    path: "/login",
    name: "login",
    component: Login,
  },
  {
    path: "/register",
    name: "register",
    component: Register,
  },
  {
    path: "/forgetPassword",
    name: "forgetPassword",
    component: ForgetPassword,
  },
  // 404页面
  {
    path: "/:pathMatch(.*)*",
    name: "notFound",
    component: NotFound,
  },
];

const frontRoutes = [
  {
    path: "/front",
    redirect: "/front/home",
    component: FrontLayout,
    meta: {
      title: "前台",
      isAdmin: false,
    },
    children: [
      {
        path: "/front/home",
        name: "frontHome",
        meta: {
          title: "前台",
          isAdmin: false,
        },
        component: FrontHome,
      },
      {
        path: "/front/userCenter",
        name: "userCenter",
        meta: {
          title: "个人中心",
          isAdmin: false,
        },
        redirect: (to) => ({
          path: ({
            UserInfoEdit: '/front/userInfo',
            PasswordEdit: '/front/passwordEdit',
            MyChatRecords: '/front/myChatRecords',
            MyAssessments: '/front/myAssessments',
            MyFavorites: '/front/myFavorites',
          })[to.query.currentMenu] || '/front/userInfo',
        }),
      },
      ...[
        { path: '/front/userInfo', title: '个人资料', component: () => import('../views/Front/UserInfoEdit.vue') },
        { path: '/front/passwordEdit', title: '修改密码', component: () => import('../views/Front/PasswordEdit.vue') },
        { path: '/front/myChatRecords', title: '我的问答', component: () => import('../views/Front/MyChatRecords.vue') },
        { path: '/front/myAssessments', title: '我的测评', component: () => import('../views/Front/MyAssessments.vue') },
        { path: '/front/myFavorites', title: '我的收藏', component: () => import('../views/Front/MyFavorites.vue') },
      ].map(({ path, title, component }) => ({ path, component, meta: { title, isAdmin: false } })),
      {
        path: "/front/aiChat",
        name: "frontAiChat",
        meta: {
          title: "智能问答",
          isAdmin: false,
        },
        component: FrontAiChat,
      },
      {
        path: "/front/articleList",
        name: "frontArticleList",
        meta: {
          title: "科普专区",
          isAdmin: false,
        },
        component: FrontArticleList,
      },
      {
        path: "/front/articleDetail",
        name: "frontArticleDetail",
        meta: {
          title: "文章详情",
          isAdmin: false,
        },
        component: FrontArticleDetail,
      },
      {
        path: "/front/scaleList",
        name: "frontScaleList",
        meta: {
          title: "心理测评",
          isAdmin: false,
        },
        component: FrontScaleList,
      },
      {
        path: "/front/scaleTest",
        name: "frontScaleTest",
        meta: {
          title: "在线测评",
          isAdmin: false,
        },
        component: FrontScaleTest,
      },
      {
        path: "/front/assessmentDetail",
        name: "frontAssessmentDetail",
        meta: {
          title: "测评详情",
          isAdmin: false,
        },
        component: FrontAssessmentDetail,
      },
      {
        path: "/front/noticeDetail",
        name: "frontNoticeDetail",
        meta: {
          title: "公告详情",
          isAdmin: false,
        },
        component: FrontNoticeDetail,
      },
              ],
  },
];

const adminRoutes = [
  {
    path: "/admin",
    name: "admin",
    redirect: "/admin/home",
    component: AdminLayout,
    meta: {
      title: "控制台",
      isAdmin: true,
    },
    children: [
      {
        path: "/admin/home",
        name: "adminHome",
        meta: {
          title: "控制台",
          isAdmin: true,
        },
        component: AdminHome,
      },
      {
        path: "/admin/userPerson",
        name: "adminUserPerson",
        meta: {
          title: "账号信息",
          isAdmin: true,
        },
        component: UserPerson,
      },
      {
        path: "/admin/passwordEdit",
        name: "passwordEdit",
        meta: {
          title: "修改密码",
          isAdmin: true,
        },
        component: PasswordEdit,
      },
      {
        path: "/admin/userList",
        name: "adminUserList",
        meta: {
          title: "账号列表",
          isAdmin: true,
        },
        component: UserList,
      },
           	 {
            path: "/Admin/articleList",
            meta: {
                title: "科普文章",
                isAdmin: true,
            },
            component: AdminArticleList
        },	
 
      	 {
            path: "/Admin/articleCommentList",
            meta: {
                title: "文章评论",
                isAdmin: true,
            },
            component: AdminArticleCommentList
        },	
 
      	 {
            path: "/Admin/articleTypeList",
            meta: {
                title: "文章分类",
                isAdmin: true,
            },
            component: AdminArticleTypeList
        },	
 
      	 {
            path: "/Admin/assessmentRecordList",
            meta: {
                title: "测评记录",
                isAdmin: true,
            },
            component: AdminAssessmentRecordList
        },	
 
      	 {
            path: "/Admin/bannerList",
            meta: {
                title: "轮播图",
                isAdmin: true,
            },
            component: AdminBannerList
        },	
 
      	 {
            path: "/Admin/chatMessageList",
            meta: {
                title: "对话消息",
                isAdmin: true,
            },
            component: AdminChatMessageList
        },	
 
      	 {
            path: "/Admin/chatSessionList",
            meta: {
                title: "对话会话",
                isAdmin: true,
            },
            component: AdminChatSessionList
        },	
 
      	 {
            path: "/Admin/documentChunkList",
            meta: {
                title: "文档分块",
                isAdmin: true,
            },
            component: AdminDocumentChunkList
        },	
 
      	 {
            path: "/Admin/favoriteList",
            meta: {
                title: "收藏",
                isAdmin: true,
            },
            component: AdminFavoriteList
        },	
 
      	 {
            path: "/Admin/hotQuestionList",
            meta: {
                title: "热门问题",
                isAdmin: true,
            },
            component: AdminHotQuestionList
        },	
 
      	 {
            path: "/Admin/knowledgeDocumentList",
            meta: {
                title: "知识文档",
                isAdmin: true,
            },
            component: AdminKnowledgeDocumentList
        },	
 
      	 {
            path: "/Admin/knowledgeTypeList",
            meta: {
                title: "知识分类",
                isAdmin: true,
            },
            component: AdminKnowledgeTypeList
        },	
 
      	 {
            path: "/Admin/noticeList",
            meta: {
                title: "公告信息",
                isAdmin: true,
            },
            component: AdminNoticeList
        },	
 
      	 {
            path: "/Admin/noticeTypeList",
            meta: {
                title: "公告分类",
                isAdmin: true,
            },
            component: AdminNoticeTypeList
        },	
 
      	 {
            path: "/Admin/qaFeedbackList",
            meta: {
                title: "问答反馈",
                isAdmin: true,
            },
            component: AdminQaFeedbackList
        },	
 
      	 {
            path: "/Admin/scaleInfoList",
            meta: {
                title: "测评量表",
                isAdmin: true,
            },
            component: AdminScaleInfoList
        },
        {
            path: "/admin/scaleInfoEdit",
            meta: {
                title: "编辑量表",
                isAdmin: true,
            },
            component: AdminScaleInfoEdit
        },	
 
      	 {
            path: "/Admin/scaleQuestionList",
            meta: {
                title: "测评题目",
                isAdmin: true,
            },
            component: AdminScaleQuestionList
        },	
 
      	 {
            path: "/Admin/scoreRuleList",
            meta: {
                title: "评分规则",
                isAdmin: true,
            },
            component: AdminScoreRuleList
        },
        {
            path: "/admin/userStatistics",
            meta: {
                title: "用户统计",
                isAdmin: true,
            },
            component: AdminUserStatistics
        },
        {
            path: "/admin/qaStatistics",
            meta: {
                title: "问答统计",
                isAdmin: true,
            },
            component: AdminQaStatistics
        },
        {
            path: "/admin/knowledgeStatistics",
            meta: {
                title: "知识库统计",
                isAdmin: true,
            },
            component: AdminKnowledgeStatistics
        },
        {
            path: "/admin/assessmentStatistics",
            meta: {
                title: "测评统计",
                isAdmin: true,
            },
            component: AdminAssessmentStatistics
        },
 
 
    ],
  },
];

// 创建路由实例
const router = createRouter({
  history: createWebHashHistory(),
  routes: [...routes, ...adminRoutes, ...frontRoutes],
});

// 路由前置守卫
router.beforeEach(async (to, from, next) => {
  // 开启进度条
  NProgress.start();

  // 获取 store 实例
  const commonStore = useCommonStore();

  // 可以在这里处理路由权限等逻辑
  if (commonStore.token) {
    //判断是否有用户信息
    if (commonStore.hasUserInfo == false) {
      //获取账号信息
      await commonStore.getInfo();
      //获取的账号id为空或者为0
      if (commonStore.userId == null || commonStore.userId == 0) {
        commonStore.logout();
      }
    }
    //如果去往的页面是后台
    if (to.meta && to.meta.isAdmin) {
      if (commonStore.roleType == "管理员") {
        next();
      } else {
        next({ path: "/" });
      }
    } else {
      if (commonStore.roleType == "管理员") {
        next({ path: "/admin" });
      } else {
        next();
      }
    }
  } else {
    if (to.meta && to.meta.isAdmin) {
      next({ path: "/login" });
    } else {
      next();
    }
  }
});

// 路由后置守卫
router.afterEach(() => {
  window.scrollTo({ top: 0 });
  // 关闭进度条
  NProgress.done();
});

export default router;
