import "./KcApp.css";
import { Suspense } from "react";
import type { KcContext } from "../../lib/kcContext";
import { useI18n } from "../../lib/i18n";
import Fallback, { defaultKcProps, type KcProps, type PageProps } from "keycloakify";
import DefaultTemplate from "keycloakify/lib/Template";
import Login from "../Login";

const kcProps: KcProps = {
    ...defaultKcProps,
    kcLoginClass: "afr-login",
    kcHeaderClass: "afr-header",
    kcHeaderWrapperClass: "afr-header-wrapper",
    kcFormCardClass: "afr-card",
    kcLabelClass: "afr-label",
    kcInputClass: "afr-input",
    kcFormSettingClass: "afr-form-settings",
    kcFormOptionsWrapperClass: "afr-link-wrapper",
    kcButtonClass: "afr-button"
};

export default function App(props: { kcContext: KcContext; }) {

    const { kcContext } = props;

    const i18n = useI18n({ kcContext });

    if (i18n === null) {
        return null;
    }

    const pageProps: Omit<PageProps<any, typeof i18n>, "kcContext"> = {
        i18n,
        // Here we have overloaded the default template, however you could use the default one with:  
        //Template: DefaultTemplate,
        Template: DefaultTemplate,
        // Wether or not we should download the CSS and JS resources that comes with the default Keycloak theme.  
        doFetchDefaultThemeResources: true,
        ...kcProps
    };

    return (
        <Suspense>
            {(() => {

                switch (kcContext.pageId) {
                    case 'login.ftl': return <Login {...{ kcContext, ...pageProps }} />;
                    default: return <Fallback {...{ kcContext, ...pageProps }} />;
                }
            })()}
        </Suspense>
    );

}
