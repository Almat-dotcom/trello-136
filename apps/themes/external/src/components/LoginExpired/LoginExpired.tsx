import { LayoutWithCarousel } from "components/Layout";
import Alert from "components/parts/Alert";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { memo } from "react";

const LoginExpired = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext, i18n: I18n } & KcProps) => {
    const { message, url } = kcContext;
    const { msgStr } = i18n;

    return (
        <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="my-6 text-slate-900 text-2xl font-bold">{msgStr("pageExpiredTitle")}</p>
                </div>

                <div className="mt-4">
                    <p className="text-slate-900">
                        {msgStr("pageExpiredMsg1")} <a href={url.loginRestartFlowUrl} className="font-bold text-secondary-dark underline">{msgStr("doClickHere")}</a>
                    </p>
                </div>

                <div className="mt-4 mb-48">
                    <p className="text-slate-900">
                        {msgStr("pageExpiredMsg2")} <a href={url.loginUrl} className="font-bold text-secondary-dark underline">{msgStr("doClickHere")}</a>
                    </p>
                </div>
            </div>
        </LayoutWithCarousel>
    );
})

export default LoginExpired;
