import React, { memo, useRef } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "../../lib/i18n";
import { KcContext } from "lib/kc";


type KcContextLoginConfigTotp = Extract<KcContext, { pageId: "login-config-totp.ftl" }>;

const LoginConfigTotp = memo(({ kcContext, i18n, ...props }: { kcContext: KcContextLoginConfigTotp; i18n: I18n; } & KcProps) => {
     const { url, totp, mode, messagesPerField } = kcContext;
     const { msgStr } = i18n;
    const formRef = useRef<HTMLFormElement>(null);

    return (
       <div>
           <h1>{msgStr("loginTotpTitle")}</h1>
           <ol id="kc-totp-settings">
                  {mode === "manual" ? (
                            <>
                                <li>
                                    <p>{msgStr("loginTotpManualStep2")}</p>
                                    <p><span id="kc-totp-secret-key">{totp.totpSecretEncoded}</span></p>
                                     <p><a href={totp.qrUrl} id="mode-barcode">{msgStr("loginTotpScanBarcode")}</a></p>
                                </li>
                                <li>
                                    <p>{msgStr("loginTotpManualStep3")}</p>
                                    <p>
                                        <ul>
                                            <li id="kc-totp-type">{msgStr("loginTotpType")}: {msgStr(`loginTotp.${totp.policy.type}`)}</li>
                                            <li id="kc-totp-algorithm">{msgStr("loginTotpAlgorithm")}: {totp.policy.algorithm}</li>
                                            <li id="kc-totp-digits">{msgStr("loginTotpDigits")}: {totp.policy.digits}</li>
                                             {totp.policy.type === "totp" ? (
                                               <li id="kc-totp-period">{msgStr("loginTotpInterval")}: {totp.policy.period}</li>
                                              ) : (
                                                <li id="kc-totp-counter">{msgStr("loginTotpCounter")}: {totp.policy.initialCounter}</li>
                                             )}
                                        </ul>
                                    </p>
                                </li>
                            </>
                         ) : (
                             <li>
                                <p>{msgStr("loginTotpStep2")}</p>
                                  <img id="kc-totp-secret-qr-code" src={`data:image/png;base64, ${totp.totpSecretQrCode}`} alt="Figure: Barcode" /><br/>
                                <p><a href={totp.manualUrl} id="mode-manual">{msgStr("loginTotpUnableToScan")}</a></p>
                             </li>
                         )}
                        <li>
                            <p>{msgStr("loginTotpStep3")}</p>
                            <p>{msgStr("loginTotpStep3DeviceName")}</p>
                         </li>
            </ol>
             <form action={url.loginAction} id="kc-totp-settings-form" method="post"  ref={formRef}>
                 <div>
                    <label htmlFor="totp">{msgStr("authenticatorCode")}</label>
                    <input type="text" id="totp" name="totp" />
                         {messagesPerField.get('totp') && (
                             <span aria-live="polite">
                                {messagesPerField.get('totp')}
                             </span>
                         )}
                    <input type="hidden" id="totpSecret" name="totpSecret" value={totp.totpSecret} />
                    {mode &&  <input type="hidden" id="mode" name="mode" value={mode} />}
                </div>
                <div>
                    <label htmlFor="userLabel">{msgStr("loginTotpDeviceName")}</label>
                    <input type="text" id="userLabel" name="userLabel" />
                        {messagesPerField.get('userLabel') && (
                             <span aria-live="polite">
                                {messagesPerField.get('userLabel')}
                             </span>
                         )}
                </div>
                {kcContext?.isAppInitiatedAction ? (
                  <>
                     <button type="button" onClick={() => formRef.current?.submit()} >{msgStr("doSubmit")}</button>
                    <button type="button" name="cancel-aia" value="true" onClick={() => formRef.current?.submit()} >{msgStr("doCancel")}</button>
                   </>
                  ) : (
                    <button type="button" onClick={() => formRef.current?.submit()}>{msgStr("doSubmit")}</button>
                  )}
             </form>
        </div>
    );
});

export default LoginConfigTotp;