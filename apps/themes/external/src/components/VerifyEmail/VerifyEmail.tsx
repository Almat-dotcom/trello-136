import { LayoutWithCarousel } from "components/Layout";
import Alert from "components/parts/Alert";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { memo } from "react";

type KcContext_VerifyEmail = Extract<KcContext, { pageId: "login-verify-email.ftl" }>;

const VerifyEmail = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_VerifyEmail; i18n: I18n; } & KcProps) => {
    const { url, user, message } = kcContext;
    const { msgStr } = i18n;

    return (
        <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="my-8 text-slate-900 text-2xl font-bold">{msgStr("emailVerifyTitle")}</p>
                </div>
                <p className="mb-4 text-slate-800">
                    {msgStr("emailVerifyInstruction1", user?.email ?? "")}
                </p>
                <p className="text-slate-800">
                    {msgStr("emailVerifyInstruction2")}
                </p>

                <p className="mb-32 text-slate-800">
                    <a
                        href={url.loginAction}
                        className="font-bold text-secondary-dark font-semibold underline"
                    >{msgStr("doClickHere")}</a> {msgStr("emailVerifyInstruction3")}
                </p>
            </div>
        </LayoutWithCarousel>
    );
});

export default VerifyEmail;
