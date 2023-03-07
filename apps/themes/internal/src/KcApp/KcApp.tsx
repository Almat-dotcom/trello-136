import "./KcApp.css";
import { lazy, Suspense } from "react";
import type { KcContext } from "./kcContext";
import KcAppBase, { defaultKcProps } from "keycloakify";
import { useI18n } from "./i18n";
import Login from "./Login/Login";

const Terms = lazy(() => import("./Terms"));

export type Props = {
    kcContext: KcContext;
};

// Base component of Keycloak Application
// which render on keycloak page.
export default function KcApp({ kcContext }: Props) {
    const i18n = useI18n({ kcContext });

    //NOTE: Locales not yet downloaded
    if (i18n === null) {
        return null;
    }

    // In default Kc props there are lots of theme.properties 
    // values with predefined classes names.
    // You can just override preexisting classes and use it
    // inside your components and override classes inside default components.
    // Just define css classes inside ./KcApp.css and set it in props.
    const props = {
        i18n,
        ...defaultKcProps,
        // NOTE: The classes are defined in ./KcApp.css
        "kcLoginClass": "login-page",
        "kcHeaderClass": "login-header",
        "kcHeaderWrapperClass": "header-wrapper",
        "kcFormCardClass": "login-card"
    };

    return (
        <Suspense>
            {(() => {
                switch (kcContext.pageId) {
                    case "login.ftl": return <Login {...{ kcContext, ...props }} />;
                    case "terms.ftl": return <Terms {...{ kcContext, ...props }} />;
                    default: return <KcAppBase doFetchDefaultThemeResources={false} {...{ kcContext, ...props }} />;
                }
            })()}
        </Suspense>
    );

}
