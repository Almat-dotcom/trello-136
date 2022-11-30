import "./KcApp.css";
import { lazy, Suspense } from "react";
import type { KcContext } from "./kcContext";
import KcAppBase, { defaultKcProps } from "keycloakify";
import { useI18n } from "./i18n";
import Login from "./Login/Login";
import Register from "./Register/Register";

const Terms = lazy(() => import("./Terms"));

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
        // NOTE: The classes are defined in ./KcApp.css
        "kcLoginClass": "login-page",
        "kcHeaderClass": "login-header",
        "kcHeaderWrapperClass": "header-wrapper",
        "kcFormCardClass": "login-card",
        "kcFormGroupClass": "login-form-group",
        "kcInputClass": "login-form-control",
        "kcFormButtonsClass": "form-buttons",
        "kcButtonPrimaryClass": "btn-primary",
        "kcFormOptionsWrapperClass": "login-options",
        "kcFormHeaderClass": "login-card-header"
    };

    return (
        <Suspense>
            {(() => {
                switch (kcContext.pageId) {
                    case "login.ftl": return <Login {...{ kcContext, ...props }} />;
                    case "terms.ftl": return <Terms {...{ kcContext, ...props }} />;
                    case "register.ftl": return <Register {...{ kcContext, ...props }} />
                    default: return <KcAppBase doFetchDefaultThemeResources={false} {...{ kcContext, ...props }} />;
                }
            })()}
        </Suspense>
    );

}
