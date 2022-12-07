import { Layout } from "components/Layout";
import Alert from "components/parts/Alert";
import Button from "components/parts/Button";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { memo } from "react";

type KcContext_LogoutConfirm = Extract<KcContext, { pageId: "logout-confirm.ftl" }>;

const UpdatePassword = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_LogoutConfirm; i18n: I18n; } & KcProps) => {
    const { url, message, logoutConfirm } = kcContext;
    const { msgStr } = i18n;

    return (
        <Layout kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="mt-3 text-slate-900 text-2xl font-bold">{msgStr("logoutConfirmTitle")}</p>
                </div>

                <div className="my-8">
                    <p className="text-primary font-bold">{msgStr("logoutConfirmHeader")}</p>
                </div>

                <div className="mt-4">
                    <form id="kc-logout-confirm" action={url.loginAction} method="post">
                        <input type="hidden" name="session_code" value={logoutConfirm.code} onChange={() => { }} />
                        <Button type="submit" severity="primary">{msgStr("doLogout")}</Button>
                    </form>
                </div>
            </div>
        </Layout>
    );
})

export default UpdatePassword;
