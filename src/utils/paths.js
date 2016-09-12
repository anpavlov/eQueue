const admin_pref = "/admin";
export default {
    main: admin_pref + "/",
    login: admin_pref + "/login",
    signup: admin_pref + "/signup",
    queue: admin_pref + "/queue/:qid",
    create: admin_pref + "/create"
};