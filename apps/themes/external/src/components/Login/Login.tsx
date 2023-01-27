import { memo, useRef, useState } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "../../lib/i18n";
import { LayoutWithCarousel } from "../Layout";
import Alert from "../parts/Alert";
import Tabs from "components/parts/Tabs";
import { KcContextLogin } from "./Type";
import UsernamePassword from "./UsernamePassword";
import Eds from "./Eds";
import Passport from './passport.svg';
import Key from './key.svg';
import Button from "components/parts/Button";

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
            <div className="px-0 lg:px-4">
                <div className="text-center">
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="mt-3 text-slate-900 text-2xl font-bold">{msgStr("loginAccountTitle")}</p>
                </div>

                <div className="mt-4">
                    <form id="kc-login-form" className="h-120" action={url.loginAction} ref={formRef} method="post">
                        <Tabs 
                            elements={[{ id: "logpass", label: msgStr("logpass"), icon: Passport }, { id: "eds", label: msgStr("eds"), icon: Key }]} 
                            activeId={currentTab} 
                            onChange={(id) => setCurrentTab(id)}
                        />
                        <Eds kcContext={kcContext} i18n={i18n} hidden={currentTab !== "eds"} onFormSubmit={onFormSubmit}/>
                        <UsernamePassword kcContext={kcContext} i18n={i18n} hidden={currentTab !== "logpass"} onFormSubmit={onFormSubmit}/>
                    </form>

                    <p 
                        className="mt-6 text-sm text-center"
                    >
                        <span className="text-gray-500">{msgStr("noAccount")}</span>
                        <Button severity="secondary" type="link" href={url.registrationUrl}>{msgStr("doRegister")}</Button>
                    </p>
                </div>
            </div>
        </LayoutWithCarousel>
    )
})

export default Login
