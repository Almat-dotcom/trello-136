import React, { useState } from "react";
import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";

type KcContextExtendedLoginConfigTotp = Extract<KcContext, { pageId: "login-config-totp.ftl" }>;

const TotpSetup = ({ kcContext, i18n }: { kcContext: KcContextExtendedLoginConfigTotp; i18n: I18n }) => {
    const { totp } = kcContext;
    const { msgStr } = i18n;

    const [otpCode, setOtpCode] = useState(""); // Код OTP, введённый пользователем
    const [deviceName, setDeviceName] = useState(""); // Имя устройства

    if (!totp) {
        return (
            <div className="flex items-center justify-center h-screen text-center">
                <h1 className="text-2xl font-bold text-red-500">{msgStr("enterKeyManually")}</h1>
            </div>
        );
    }

    const onSubmit = () => {
        const form = document.getElementById("kc-totp-setup-form") as HTMLFormElement;
        form.submit();
    };

    return (
        <div className="px-4 lg:px-8 py-6 bg-white shadow-md rounded-lg max-w-lg mx-auto mt-10">
            <h1 className="text-3xl font-bold mb-6">{msgStr("enterKeyManually")}</h1>

            <ol className="list-decimal list-inside mb-6">
                <li className="mb-4">
                    {msgStr("enterKeyManually")}
                    <img
                        src={`data:image/png;base64,${totp.totpSecretQrCode}`}
                        alt="TOTP QR Code"
                        className="border-2 rounded-md w-48 h-48 mx-auto my-4"
                    />
                </li>
                <li className="mb-4">
                    {msgStr("enterKeyManually")}
                    <code className="block bg-gray-100 p-2 rounded text-sm font-mono mt-2">
                        {totp.totpSecret}
                    </code>
                </li>
                <li className="mb-4">
                    {msgStr("enterKeyManually")}
                </li>
            </ol>

            <form id="kc-totp-setup-form" method="post" action={kcContext.url.loginAction} className="space-y-4">
                <InputField
                    fieldName="totp"
                    label={msgStr("authenticatorCode")}
                    type="text"
                    value={otpCode}
                    onChange={(e) => setOtpCode(e.target.value)}
                    required
                />
                <InputField
                    fieldName="userLabel"
                    label={msgStr("deviceName")}
                    type="text"
                    value={deviceName}
                    onChange={(e) => setDeviceName(e.target.value)}
                    required
                />
                <input type="hidden" name="totpSecret" value={totp.totpSecret} />

                <div className="flex justify-between">
                    <Button
                        type="button"
                        severity="link-primary"
                        onClick={() => (window.location.href = kcContext.url.cancelUrl || "#")}
                    >
                        {msgStr("doCancel")}
                    </Button>
                    <Button severity="primary" type="submit" onClick={onSubmit}>
                        {msgStr("doSubmit")}
                    </Button>
                </div>
            </form>
        </div>
    );
};

export default TotpSetup;
