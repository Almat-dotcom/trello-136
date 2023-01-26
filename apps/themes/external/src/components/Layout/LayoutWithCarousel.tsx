import Carousel from "components/parts/Carousel";
import Footer from "components/parts/Footer";
import Header from "components/parts/Header";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { ReactNode } from "react";

type LayoutProps = {
    children?: ReactNode
};

const LayoutWithCarousel = ({ children, kcContext, i18n, ...props }: { children: any, kcContext: KcContext, i18n: I18n } & LayoutProps) => {
    const { locale } = kcContext
    const { currentLanguageTag, supported } = locale ?? { currentLanguageTag: "en", supported: [] }

    return (
        <div className="bg-white">
            <Header current={currentLanguageTag} langs={supported} i18n={i18n} />
            <div className="flex justify-center items-stretch min-h-screen">
                <div className="hidden bg-gradient-to-tr lg:block lg:w-3/5">
                    <div className="flex flex-col items-center justify-between p-4">
                        <div className="w-full h-24">
                        </div>

                        <div className="flex items-center justify-center w-full">
                            <Carousel i18n={i18n} />
                        </div>

                        <div></div>
                    </div>
                </div>
                <div className="flex flex-col justify-between w-full max-w-md px-6 mx-auto lg:w-2/5">
                    <div className="flex justify-between lg:justify-end p-2">
                    </div>
                    {children}
                    <div className="w-20">

                    </div>
                </div>
            </div>
            <Footer i18n={i18n} />
        </div>
    );
}

export default LayoutWithCarousel;
