import { PageProps } from "keycloakify";
import { clsx } from "keycloakify/lib/tools/clsx";
import { useConstCallback } from "keycloakify/lib/tools/useConstCallback";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kcContext";
import { signAuthXml } from "lib/ncalayer";
import { CancelledByUser, ConnectionLost } from "lib/ncalayer/NCALayer";
import { FormEventHandler, useRef, useState } from "react";

const NCAMessage = ({ message, i18n }: { message: string, i18n: I18n }) => {
    const dark = (message: string) => {
        switch (message) {
            case 'ncaSignInProgress': return '#8b8d8f';
            case 'ncaSignFinished': return '#3f9c35';
            case 'ncaCancelled': return '#ec7a08';
            default: return '#cc0000';
        }
    }
    const light = (message: string) => {
        switch (message) {
            case 'ncaSignInProgress': return '#f5f5f5';
            case 'ncaSignFinished': return '#e9f4e9';
            case 'ncaCancelled': return '#fdf2e5';
            default: return '#ffffff';
        }
    }

    return (
        <div style={{
            color: dark(message),
            backgroundColor: light(message),
            border: `2px solid ${dark(message)}`,
            borderRadius: '5px',
            paddingTop: '1rem',
            paddingBottom: '1rem',
            paddingLeft: '1.5rem',
            paddingRight: '1.5rem',
            fontSize: '1.5rem'
        }}>
            {i18n.advancedMsgStr(message)}
        </div>
    );
}

const Login = (props: PageProps<Extract<KcContext, { pageId: 'login.ftl'; }>, I18n>) => {
    const { kcContext, i18n, doFetchDefaultThemeResources = true, Template, ...kcProps } = props;

    const { social, realm, url, usernameEditDisabled, login, auth, registrationDisabled, client } = kcContext;

    const { msg, msgStr } = i18n;

    const [inputDisabled, setInputDisabled] = useState(false);
    const [ncaMessage, setNcaMessage] = useState('');
    const edsRef = useRef<HTMLInputElement>(null);

    const onSubmit = useConstCallback<FormEventHandler<HTMLFormElement>>(async e => {
        e.preventDefault();

        setInputDisabled(true);
        setNcaMessage('ncaSignInProgress');
        const xml = `<Authentication><signature>${client.clientId}${realm.name}</signature></Authentication>`;
        try {
            const signature = await signAuthXml(xml);
            edsRef.current!.value = signature;
            setNcaMessage('ncaSignFinished');

            const formElement = e.target as HTMLFormElement;

            //NOTE: Even if we login with email Keycloak expect username and password in
            //the POST request.
            formElement.querySelector("input[name='email']")?.setAttribute("name", "username");

            formElement.submit();
        } catch (error) {
            setInputDisabled(false);
            if (error === ConnectionLost) {
                setNcaMessage('ncaConnectionLost');
            } else if (error === CancelledByUser) {
                setNcaMessage('ncaCancelled');
            } else {
                setNcaMessage('ncaError');
            }
        }
    });

    return (
        <Template
            {...{ kcContext, i18n, doFetchDefaultThemeResources, ...kcProps }}
            displayInfo={social.displayInfo}
            displayWide={realm.password && social.providers !== undefined}
            headerNode={msg("doLogIn")}
            formNode={
                <div id="kc-form" className={clsx(realm.password && social.providers !== undefined && kcProps.kcContentWrapperClass)}>
                    <div
                        id="kc-form-wrapper"
                        className={clsx(
                            realm.password && social.providers && [kcProps.kcFormSocialAccountContentClass, kcProps.kcFormSocialAccountClass]
                        )}
                    >
                        {ncaMessage && (
                            <NCAMessage message={ncaMessage} i18n={i18n} />
                        )}
                        {realm.password && (
                            <form id="kc-form-login" onSubmit={onSubmit} action={url.loginAction} method="post">
                                <div className={clsx(kcProps.kcFormGroupClass)}>
                                    {(() => {
                                        const label = !realm.loginWithEmailAllowed
                                            ? "username"
                                            : realm.registrationEmailAsUsername
                                                ? "email"
                                                : "usernameOrEmail";

                                        const autoCompleteHelper: typeof label = label === "usernameOrEmail" ? "username" : label;

                                        return (
                                            <>
                                                <label htmlFor={autoCompleteHelper} className={clsx(kcProps.kcLabelClass)}>
                                                    {msg(label)}
                                                </label>
                                                <input
                                                    tabIndex={1}
                                                    id={autoCompleteHelper}
                                                    className={clsx(kcProps.kcInputClass)}
                                                    //NOTE: This is used by Google Chrome auto fill so we use it to tell
                                                    //the browser how to pre fill the form but before submit we put it back
                                                    //to username because it is what keycloak expects.
                                                    name={autoCompleteHelper}
                                                    defaultValue={login.username ?? ""}
                                                    type="text"
                                                    {...(usernameEditDisabled || inputDisabled
                                                        ? { "disabled": true }
                                                        : {
                                                            "autoFocus": true,
                                                            "autoComplete": "off"
                                                        })}
                                                />
                                            </>
                                        );
                                    })()}
                                </div>
                                <div className={clsx(kcProps.kcFormGroupClass)}>
                                    <label htmlFor="password" className={clsx(kcProps.kcLabelClass)}>
                                        {msg("password")}
                                    </label>
                                    <input
                                        tabIndex={2}
                                        id="password"
                                        className={clsx(kcProps.kcInputClass)}
                                        name="password"
                                        type="password"
                                        autoComplete="off"
                                        disabled={inputDisabled}
                                    />
                                </div>
                                <div className={clsx(kcProps.kcFormGroupClass, kcProps.kcFormSettingClass)}>
                                    <div id="kc-form-options">
                                        {realm.rememberMe && !usernameEditDisabled && (
                                            <div className="checkbox">
                                                <label>
                                                    <input
                                                        tabIndex={3}
                                                        id="rememberMe"
                                                        name="rememberMe"
                                                        type="checkbox"
                                                        disabled={inputDisabled}
                                                        {...(login.rememberMe
                                                            ? {
                                                                "checked": true
                                                            }
                                                            : {})}
                                                    />
                                                    {msg("rememberMe")}
                                                </label>
                                            </div>
                                        )}
                                    </div>
                                    <div className={clsx(kcProps.kcFormOptionsWrapperClass)}>
                                        {(realm.resetPasswordAllowed && !inputDisabled) && (
                                            <span>
                                                <a tabIndex={5} href={url.loginResetCredentialsUrl}>
                                                    {msg("doForgotPassword")}
                                                </a>
                                            </span>
                                        )}
                                    </div>
                                </div>
                                <div id="kc-form-buttons" className={clsx(kcProps.kcFormGroupClass)}>
                                    <input
                                        type="hidden"
                                        id="id-hidden-input"
                                        name="credentialId"
                                        {...(auth?.selectedCredential !== undefined
                                            ? {
                                                "value": auth.selectedCredential
                                            }
                                            : {})}
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
                                        disabled={inputDisabled}
                                    />
                                </div>
                            </form>
                        )}
                    </div>
                    {realm.password && social.providers !== undefined && (
                        <div id="kc-social-providers" className={clsx(kcProps.kcFormSocialAccountContentClass, kcProps.kcFormSocialAccountClass)}>
                            <ul
                                className={clsx(
                                    kcProps.kcFormSocialAccountListClass,
                                    social.providers.length > 4 && kcProps.kcFormSocialAccountDoubleListClass
                                )}
                            >
                                {social.providers.map(p => (
                                    <li key={p.providerId} className={clsx(kcProps.kcFormSocialAccountListLinkClass)}>
                                        <a href={p.loginUrl} id={`zocial-${p.alias}`} className={clsx("zocial", p.providerId)}>
                                            <span>{p.displayName}</span>
                                        </a>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            }
            infoNode={
                realm.password &&
                realm.registrationAllowed &&
                !registrationDisabled && (
                    <div id="kc-registration">
                        <span>
                            {msg("noAccount")}
                            <a tabIndex={6} href={url.registrationUrl}>
                                {msg("doRegister")}
                            </a>
                        </span>
                    </div>
                )
            }
        />
    );
}

export default Login;
