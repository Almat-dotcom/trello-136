import { PageProps } from "keycloakify";
import { clsx } from "keycloakify/lib/tools/clsx";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kcContext";
import { signAuthXml } from "lib/ncalayer";
import { CancelledByUser, ConnectionLost } from "lib/ncalayer/NCALayer";
import { useRef, useState } from "react";

import { AfrHeader } from "../parts/AfrHeader";
import { LocaleSelector } from "../parts/LocaleSelector";
import { MessageAlert } from "../parts/MessageAlert";
import { NCAMessage } from "../parts/NCAMessage";

type KcContext_Login = Extract<KcContext, { pageId: "login.ftl" }>;

const Login = (props: PageProps<KcContext_Login, I18n>) => {
    const { kcContext, i18n, ...kcProps } = props;
    const { social, realm, url, usernameEditDisabled, login, client, message, locale } = kcContext;
    const { msg, msgStr } = i18n;

    const [ncaMessage, setNcaMessage] = useState("");
    const edsRef = useRef<HTMLInputElement>(null);
    const formRef = useRef<HTMLFormElement>(null);
    const usernameRef = useRef<HTMLInputElement>(null);
    const passwordRef = useRef<HTMLInputElement>(null);
    const rememberMeRef = useRef<HTMLInputElement>(null);
    const loginRef = useRef<HTMLInputElement>(null);

    const setFieldsDisabled = (disabled: boolean) => {
        if (usernameRef.current) usernameRef.current.disabled = disabled;
        if (passwordRef.current) passwordRef.current.disabled = disabled;
        if (rememberMeRef.current) rememberMeRef.current.disabled = disabled;
        if (loginRef.current) loginRef.current.disabled = disabled;
    };

    const onSubmit = async () => {
        try {
            setFieldsDisabled(true);
            setNcaMessage("ncaSignProgress");

            const xml = `<Authentication><signature>${client.clientId}${realm.name}</signature></Authentication>`;
            const signature = await signAuthXml(xml);

            if (edsRef.current) {
                edsRef.current.value = signature;
            }
            setNcaMessage("ncaSignFinished");

            setFieldsDisabled(false);

            formRef.current?.submit();
        } catch (error) {
            setFieldsDisabled(false);

            if (error === ConnectionLost) {
                setNcaMessage("ncaConnectionLost");
            } else if (error === CancelledByUser) {
                setNcaMessage("ncaCancelled");
            } else {
                setNcaMessage("ncaError");
            }
        }
    };

    return (
        <div className="afr-login">
            <AfrHeader i18n={i18n} />

            <div className="afr-card">
                <header className="login-pf-header">
                    {locale && <LocaleSelector locale={locale} i18n={i18n} />}

                    <h1 id="kc-page-title">
                        <span>{msg("loginHeader")}</span>
                    </h1>

                    <MessageAlert message={message} i18n={i18n} />
                </header>

                <div
                    id="kc-form"
                    className={clsx(
                        realm.password && social.providers !== undefined && kcProps.kcContentWrapperClass
                    )}
                >
                    <div
                        id="kc-form-wrapper"
                        className={clsx(
                            realm.password && social.providers && [
                                kcProps.kcFormSocialAccountContentClass,
                                kcProps.kcFormSocialAccountClass
                            ]
                        )}
                    >
                        <NCAMessage message={ncaMessage} i18n={i18n} />

                        {realm.password && (
                            <form
                                id="kc-form-login"
                                ref={formRef}
                                action={url.loginAction}
                                method="post"
                                autoComplete="on"
                            >
                                <div className={clsx(kcProps.kcFormGroupClass)}>
                                    <label
                                        htmlFor="username"
                                        className={clsx(kcProps.kcLabelClass)}
                                    >
                                        {msg("email")}
                                    </label>
                                    <div className="afr-input-wrapper">
                                        <input
                                            id="username"
                                            className={clsx(kcProps.kcInputClass)}
                                            name="username"
                                            type="text"
                                            ref={usernameRef}
                                            autoComplete="username"
                                        />
                                    </div>
                                </div>

                                <div className={clsx(kcProps.kcFormGroupClass)}>
                                    <label
                                        htmlFor="password"
                                        className={clsx(kcProps.kcLabelClass)}
                                    >
                                        {msg("password")}
                                    </label>
                                    <div className="afr-input-wrapper">
                                        <input
                                            id="password"
                                            className={clsx(kcProps.kcInputClass)}
                                            name="password"
                                            type="password"
                                            ref={passwordRef}
                                            autoComplete="current-password"
                                        />
                                    </div>
                                </div>

                                <div
                                    className={clsx(
                                        kcProps.kcFormGroupClass,
                                        kcProps.kcFormSettingClass
                                    )}
                                >
                                    <div
                                        style={{
                                            display: "flex",
                                            alignItems: "center",
                                            justifyContent: "space-between",
                                            width: "100%"
                                        }}
                                    >
                                        {realm.rememberMe && !usernameEditDisabled && (
                                            <div className="checkbox">
                                                <label>
                                                    <input
                                                        tabIndex={3}
                                                        id="rememberMe"
                                                        name="rememberMe"
                                                        type="checkbox"
                                                        ref={rememberMeRef}
                                                        defaultChecked={!!login.rememberMe}
                                                    />
                                                    {msg("rememberMe")}
                                                </label>
                                            </div>
                                        )}
                                        {realm.resetPasswordAllowed && (
                                            <a
                                                href={url.loginResetCredentialsUrl}
                                                className="text-sm text-secondary-dark font-semibold"
                                            >
                                                {msgStr("doForgotPassword")}
                                            </a>
                                        )}
                                    </div>
                                </div>

                                <div
                                    id="kc-form-buttons"
                                    className={clsx(kcProps.kcFormGroupClass)}
                                >
                                    <input
                                        type="hidden"
                                        id="id-hidden-input"
                                        name="credentialId"
                                        value="password"
                                    />
                                    <input
                                        type="hidden"
                                        id="eds"
                                        name="eds"
                                        ref={edsRef}
                                    />
                                    <input
                                        tabIndex={4}
                                        className={clsx(
                                            kcProps.kcButtonClass,
                                            kcProps.kcButtonPrimaryClass,
                                            kcProps.kcButtonBlockClass,
                                            kcProps.kcButtonLargeClass
                                        )}
                                        name="login"
                                        id="kc-login"
                                        type="submit"
                                        value={msgStr("doLogIn")}
                                        ref={loginRef}
                                        onClick={e => {
                                            e.preventDefault();
                                            onSubmit();
                                        }}
                                    />
                                </div>
                            </form>
                        )}
                    </div>

                    {realm.password && social.providers !== undefined && (
                        <div
                            id="kc-social-providers"
                            className={clsx(
                                kcProps.kcFormSocialAccountContentClass,
                                kcProps.kcFormSocialAccountClass
                            )}
                        >
                            <ul
                                className={clsx(
                                    kcProps.kcFormSocialAccountListClass,
                                    social.providers.length > 4 &&
                                        kcProps.kcFormSocialAccountDoubleListClass
                                )}
                            >
                                {social.providers.map(p => (
                                    <li
                                        key={p.providerId}
                                        className={clsx(kcProps.kcFormSocialAccountListLinkClass)}
                                    >
                                        <a
                                            href={p.loginUrl}
                                            id={`zocial-${p.alias}`}
                                            className={clsx("zocial", p.providerId)}
                                        >
                                            <span>{p.displayName}</span>
                                        </a>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Login;
