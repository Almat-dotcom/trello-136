import {memo, useState } from "react";
import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { Layout } from "components/Layout";
import Alert from "components/parts/Alert";

type KcContextExtendedLoginConfigTotp = Extract<KcContext, { pageId: "login-config-totp.ftl" }>;

const TotpSetup = memo(({ kcContext, i18n }: { kcContext: KcContextExtendedLoginConfigTotp; i18n: I18n; } & KcProps) => {
    const { totp, url, message } = kcContext;
    const { msgStr } = i18n;

    const [otp, setOtp] = useState("");
    const [deviceName, setDeviceName] = useState("");

    const onSubmit = () => {
        const form = document.getElementById("kc-totp-setup-form") as HTMLFormElement;
        form.submit();
    };

    return (
        <Layout kcContext={kcContext} i18n={i18n}>
            <div className="px-4 lg:px-8">
                <div>
                    {message && (
                        <Alert i18n={i18n} type={message.type} message={message.summary} />
                    )}
                    <h1 className="text-3xl font-bold text-gray-900 mt-4">{msgStr("loginTotpTitle")}</h1>
                </div>

                <form id="totp-setup-form" action={url.loginAction} method="post" className="mt-6 space-y-6">
                    <div>
                        <p>{msgStr("loginTotpStep1")}</p>
                        <img
                            src={`data:image/png;base64,${totp.totpSecretQrCode}`}
                            alt="TOTP QR Code"
                            className="border rounded-lg shadow-md max-w-sm mx-auto"
                        />
                        <p className="mt-4">{msgStr("loginTotpManualStep2")}</p>
                        <div className="bg-gray-100 p-2 rounded-md text-gray-800 font-mono">{totp.totpSecret}</div>
                    </div>

                    <div>
                        <InputField
                            fieldName="totp"
                            type="text"
                            label={msgStr("authenticatorCode")}
                            placeholder="123456"
                            value={otp}
                            onChange={(e) => setOtp(e.target.value)}
                            required
                        />
                    </div>

                    <div>
                        <InputField
                            fieldName="userLabel"
                            type="text"
                            label={msgStr("loginTotpDeviceName")}
                            placeholder={msgStr("loginTotpDeviceName")}
                            value={deviceName}
                            onChange={(e) => setDeviceName(e.target.value)}
                            required
                        />
                    </div>

                    <div className="flex justify-between items-center">
                        <Button
                            severity="primary"
                            type="button"
                            onClick={onSubmit}
                        >
                            {msgStr("doSubmit")}
                        </Button>
                        {url.loginRestartFlowUrl && (
                            <Button
                                severity="secondary"
                                type="link"
                                href={url.loginRestartFlowUrl}
                            >
                                {msgStr("doCancel")}
                            </Button>
                        )}
                    </div>
                </form>
            </div>
        </Layout>
    );
});

export default TotpSetup;
