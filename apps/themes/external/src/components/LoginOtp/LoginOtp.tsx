import React, { memo, useState } from "react";
import type { KcProps } from "keycloakify";
import type { I18n } from "lib/i18n";
import Button from "components/parts/Button";
import { InputField } from "components/parts/Input";
import { KcContext } from "lib/kc";

type KcContextExtendedOTPForm = Extract<KcContext, { pageId: "login-otp.ftl" }>;


const LoginOtp = memo(({ kcContext, i18n }: { kcContext: KcContextExtendedOTPForm; i18n: I18n; } & KcProps) => {
    const { url } = kcContext;
    const { msgStr } = i18n;
    const [otp, setOtp] = useState("");

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        const form = document.getElementById("otp-form") as HTMLFormElement;
        if (form) form.submit();
    };

    return (
        <form
            id="otp-form"
            method="post"
            action={url.loginAction}
            onSubmit={handleSubmit}
            className="flex flex-col items-center p-6 max-w-sm mx-auto bg-white shadow-lg rounded-lg"
        >
            <h1 className="text-2xl font-bold mb-4">{msgStr("enterOtp")}</h1>
            <p className="text-gray-600 mb-6">{msgStr("setupTotp")}</p>

            <InputField
                fieldName="otp"
                type="text"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                required
                label={msgStr("enterOtp")}
                placeholder="123456"
            />

            <Button type="submit" severity="primary">
                {msgStr("confirm")}
            </Button>
        </form>
    );
});

export default LoginOtp;
