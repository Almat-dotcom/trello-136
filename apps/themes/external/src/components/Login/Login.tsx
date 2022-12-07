import { memo, useRef, useState } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "../../lib/i18n";
import { LayoutWithCarousel } from "../Layout";
import Alert from "../parts/Alert";
import Tabs from "components/parts/Tabs";
import { KcContextLogin } from "./Type";
import UsernamePassword from "./UsernamePassword";
import Eds from "./Eds";

const Login = memo(({ kcContext, i18n, ...props }: { kcContext: KcContextLogin; i18n: I18n; } & KcProps) => {
    const { url, message } = kcContext;
    const { msgStr } = i18n;

    const formRef = useRef<HTMLFormElement>(null);

    const onFormSubmit = () => {
        formRef.current?.submit()
    }

    const [currentTab, setCurrentTab] = useState("logpass")

    return (
        <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
            <div>
                <div className="text-center">
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="mt-3 text-slate-900 text-2xl font-bold">{msgStr("loginAccountTitle")}</p>
                </div>

                <div className="mt-4">
                    <form id="kc-login-form" className="h-80" action={url.loginAction} ref={formRef} method="post">
                        <Tabs 
                            elements={[{ id: "eds", label: msgStr("eds") }, { id: "logpass", label: msgStr("logpass") }]} 
                            activeId={currentTab} 
                            onChange={(id) => setCurrentTab(id)}
                        />
                        <Eds kcContext={kcContext} i18n={i18n} hidden={currentTab !== "eds"} onFormSubmit={onFormSubmit}/>
                        <UsernamePassword kcContext={kcContext} i18n={i18n} hidden={currentTab !== "logpass"} onFormSubmit={onFormSubmit}/>
                    </form>

                    <p 
                        className="mt-6 text-sm text-center text-gray-400"
                    >
                        {msgStr("noAccount")}
                        <a 
                            href={url.registrationUrl} 
                            className="ml-4 text-blue-500 hover:text-primary focus:outline-none focus:underline hover:underline"
                        >
                            {msgStr("doRegister")}
                        </a>
                    </p>
                </div>
            </div>
        </LayoutWithCarousel>
    )
})

export default Login
