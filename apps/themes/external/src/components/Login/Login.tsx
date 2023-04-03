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
import Email from './email.svg';
import Button from "components/parts/Button";
import ButtonChoise from "components/parts/ButtonChoise";

const Login = memo(({ kcContext, i18n, ...props }: { kcContext: KcContextLogin; i18n: I18n; } & KcProps) => {
    const { url, message } = kcContext;
    const { msgStr } = i18n;

    const formRef = useRef<HTMLFormElement>(null);

    const tabs = {
        resident: {
            physical: [
                { id: "logpass", label: msgStr("logpass"), icon: Passport },
                { id: "email", label: msgStr("email"), icon: Email },
                { id: "eds", label: msgStr("eds"), icon: Key }
            ],
            legal: [
                { id: "eds", label: msgStr("eds"), icon: Key }
            ]
        },
        nonResident: {
            physical: [
                { id: "email", label: msgStr("email"), icon: Email }
            ],
            legal: [
                { id: "email", label: msgStr("email"), icon: Email }
            ]
        }
    }

    const [currentTab, setCurrentTab] = useState("");

    const [residency, setResidency] = useState<"resident" | "nonResident" | undefined>(undefined);
    const setResident = () => {
        setResidency("resident");
    }
    const setNonResident = () => {
        setResidency("nonResident");
    }
    const resetResidency = () => {
        setResidency(undefined);
    }

    const [type, setType] = useState<"physical" | "legal" | undefined>(undefined);
    const setPhysical = () => {
        setType("physical");
        setCurrentTab(tabs[residency ?? "resident"].physical[0].id);
    }
    const setLegal = () => {
        setType("legal");
        setCurrentTab(tabs[residency ?? "resident"].legal[0].id);
    }
    const resetType = () => {
        setType(undefined);
        setCurrentTab("");
    }

    const onFormSubmit = () => {
        formRef.current?.submit()
    }

    return (
        <LayoutWithCarousel kcContext={kcContext} i18n={i18n}>
            <div className="px-0 lg:px-4">
                <div>
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}

                    <p className="mt-3 text-slate-900 text-3xl font-bold">{msgStr("loginAccountTitle")}</p>
                </div>

                <div className="mt-4 w-full">
                    <form id="kc-login-form" className="w-full h-120" action={url.loginAction} ref={formRef} method="post">
                        {!residency ? (
                            <div>
                                <ButtonChoise choises={[{ name: msgStr("resident"), onSelect: setResident }, { name: msgStr("nonResident"), onSelect: setNonResident }]} />
                            </div>
                        ) : null
                        }
                        {residency && !type ? (
                            <div>
                                <ButtonChoise choises={[{ name: msgStr("physical"), onSelect: setPhysical }, { name: msgStr("legal"), onSelect: setLegal }]} />

                                <div className="w-min">
                                    <Button type="button" severity="link-primary" onClick={resetResidency}>&laquo;{msgStr("doBack")}</Button>
                                </div>
                            </div>
                        ) : null
                        }
                        {residency && type ? (
                            <div>
                                <Tabs
                                    elements={tabs[residency][type]}
                                    activeId={currentTab}
                                    onChange={(id) => setCurrentTab(id)} />

                                <Eds kcContext={kcContext} i18n={i18n} hidden={currentTab !== "eds"} onFormSubmit={onFormSubmit} />
                                {currentTab === "email" ? (
                                    <UsernamePassword
                                        kcContext={kcContext}
                                        i18n={i18n}
                                        hidden={false}
                                        variant="email"
                                        onFormSubmit={onFormSubmit}
                                    />
                                ) : null}
                                {currentTab === 'logpass' ? (
                                    (
                                        <UsernamePassword
                                            kcContext={kcContext}
                                            i18n={i18n}
                                            hidden={false}
                                            variant="iin"
                                            onFormSubmit={onFormSubmit}
                                        />
                                    )
                                ) : null}
                                <div className="w-min">
                                    <Button type="button" severity="link-primary" onClick={resetType}>&laquo;{msgStr("doBack")}</Button>
                                </div>
                            </div>
                        ) : null}
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
