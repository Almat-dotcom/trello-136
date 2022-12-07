import Carousel from "components/parts/Carousel";
import Lang from "components/parts/Lang";
import { I18n } from "lib/i18n";
import { KcContext } from "lib/kc";
import { ReactNode } from "react";
import { ReactComponent as Logo } from './logo.svg';

type LayoutProps = {
    children?: ReactNode
};

const LayoutWithCarousel = ({ children, kcContext, i18n, ...props }: { children: any, kcContext: KcContext, i18n: I18n } & LayoutProps) => {
    const { locale } = kcContext
    const { currentLanguageTag, supported } = locale ?? { currentLanguageTag: "en", supported: [] }

    return (
        <div className="bg-white font-serif">
            <div className="flex justify-center items-stretch min-h-screen">
                <div className="hidden bg-gradient-to-tr from-back-dark to-back-light lg:block lg:w-2/3">
                    <div className="flex flex-col items-center justify-between p-4">
                        <div className="w-full">
                            <Logo />
                        </div>

                        <div className="flex items-center justify-center w-full">
                            <Carousel />
                        </div>

                        <div></div>
                    </div>
                </div>
                <div className="flex flex-col justify-between w-full max-w-md px-6 mx-auto lg:w-1/3">
                    <div className="flex justify-between lg:justify-end p-2">
                        <div className="block lg:hidden"><Logo /></div>
                        <Lang current={currentLanguageTag} langs={supported} i18n={i18n} />
                    </div>
                    {children}
                    <div className="w-20">

                    </div>
                </div>
            </div>
        </div>
    );
}

export default LayoutWithCarousel;
