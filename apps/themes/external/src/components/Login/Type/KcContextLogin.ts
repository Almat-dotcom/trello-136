import { KcContext } from "lib/kc";

export type KcContextLogin = Extract<KcContext, { pageId: "login.ftl" }>;
