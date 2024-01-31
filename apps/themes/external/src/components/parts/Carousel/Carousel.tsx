import News3 from "./news_3.jpg";
import { useState } from "react";
import { I18n } from "lib/i18n";

const Carousel = ({ i18n } : { i18n: I18n }) => {
    const { msgStr } = i18n;
    const [page, setPage] = useState(0);
    return (
        <div className="w-5/6 py-8">
            <div>
                <div className="flex flex-col items-center m-4">
                    <div className="m-3 w-11/12">
                        <img
                            src={News3}
                            className="w-full rounded-3xl" />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Carousel;
