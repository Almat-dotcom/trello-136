import { Layout } from "components/Layout";
import Button from "components/parts/Button";
import { KcProps } from "keycloakify";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import React, { memo } from "react";

type KcContext_Otp = Extract<KcContext, { pageId: "otp.ftl" }>;

const GoogleAuthenticator = memo(({ kcContext, i18n, ...props }: { kcContext: KcContext_Otp; i18n: I18n; } & KcProps) => {

    return (    
        <Layout kcContext={kcContext} i18n={i18n}>
            <h1>Enter the otp</h1>
            <form action={kcContext.url.loginAction} method="POST">
                <input type="text" name="otp" placeholder="Enter OTP" required />
                <Button severity="primary" type="submit">Submit</Button>
            </form>
        </Layout>
    );
});

export default GoogleAuthenticator;
