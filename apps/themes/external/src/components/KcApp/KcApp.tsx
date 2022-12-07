import "./KcApp.css";
import "@fontsource/roboto";
import "primereact/resources/primereact.min.css";
import "primereact/resources/themes/saga-orange/theme.css";

import { Suspense } from "react";
import type { KcContext } from "../../lib/kc";
import KcAppBase, { defaultKcProps, KcContextBase } from "keycloakify";
import { useI18n } from "../../lib/i18n";
import Login from "../Login";
import Register from "../Register";
import VerifyEmail from "../VerifyEmail";
import UpdatePassword from "components/UpdatePassword";
import LogoutConfirm from "components/LogoutConfirm";
import ResetPassword from "components/ResetPassword";
import LoginExpired from "components/LoginExpired";
import Info from "components/Info";
import Error from "components/Error";

export type Props = {
    kcContext: KcContext;
};

export default function KcApp({ kcContext }: Props) {

    const i18n = useI18n({ kcContext });

    //NOTE: Locales not yet downloaded
    if (i18n === null) {
        return null;
    }

    const props = {
        i18n,
        ...defaultKcProps,
    };

    const defaultContext = kcContext as KcContextBase

    return (
        <Suspense>
            {(() => {
                switch (kcContext.pageId) {
                    case "login.ftl": return <Login {...{ kcContext, ...props }} />;
                    case "register.ftl": return <Register {...{ kcContext, ...props }} />;
                    case "login-verify-email.ftl": return <VerifyEmail {...{ kcContext, ...props }} />;
                    case "login-update-password.ftl": return <UpdatePassword {...{ kcContext, ...props }} />;
                    case "logout-confirm.ftl": return <LogoutConfirm {...{ kcContext, ...props }} />;
                    case "login-reset-password.ftl": return <ResetPassword {...{ kcContext, ...props }} />;
                    case "login-page-expired.ftl": return <LoginExpired {...{ kcContext, ...props }} />;
                    case "info.ftl": return <Info {...{ kcContext, ...props }} />;
                    case "error.ftl": return <Error {...{ kcContext, ...props }} />;
                    default: return <KcAppBase doFetchDefaultThemeResources={true} {...{ kcContext: defaultContext, ...props }} />;
                }
            })()}
        </Suspense>
    );
}
