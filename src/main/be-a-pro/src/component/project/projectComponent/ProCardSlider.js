import ProCard from './ProCard';
import styles from './ProCardSlider.module.css';
import { Swiper, SwiperSlide } from 'swiper/react';
import SwiperCore, { Navigation, Pagination, Autoplay } from "swiper";
import 'swiper/scss'
import 'swiper/scss/navigation'
import 'swiper/scss/pagination'
import { useEffect } from 'react';

SwiperCore.use([Navigation, Pagination, Autoplay])

function ProCardSlider(props) {

    // const part1 = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15];
    // const part2 = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15];
    // const part3 = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15];
    // const part4 = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15];
    // const part5 = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15];

    useEffect(() => {
        var activePage = document.getElementsByClassName("swiper-pagination-bullet-active")[1];
        activePage.style.backgroundColor = "#ff7827";
        activePage.style.width = "20px"
        activePage.style.borderRadius = "20px 20px";
        activePage.style.transition = "all 0.5s";

        var paginationDiv = document.getElementsByClassName("swiper-pagination")[1]
        paginationDiv.style.position = "static";
        paginationDiv.style.marginTop = "24px";
    })

    return (
        <div className={styles.container}>
            <div className={styles.proCardSection}>
                <Swiper className={styles.swiper}
                    spaceBetween={50}
                    slidesPerView={1}
                    pagination={{ clickable: true }}
                    onSlideChange={(e) => {
                        for (let i = 0; i<e.pagination.bullets.length; i++) {
                            if (e.pagination.bullets[i].classList.contains("swiper-pagination-bullet-active")) {
                                e.pagination.bullets[i].style.backgroundColor = "#ff7827";
                                e.pagination.bullets[i].style.width = "20px";
                                e.pagination.bullets[i].style.borderRadius = "20px 20px";
                                e.pagination.bullets[i].style.transition = "all 0.5s";
                            }
                            else {
                                e.pagination.bullets[i].style.width = "8px";
                                e.pagination.bullets[i].style.borderRadius = "50%";
                                e.pagination.bullets[i].style.transition = "all 0.5s";
                            }
                        };
                }}>
                    <SwiperSlide>
                            <div className={styles.slideSection}>
                                <div className={styles.proCard}>
                                    <ProCard/>
                                    <ProCard/>
                                    <ProCard/>
                                    <ProCard/>
                                    <ProCard/>
                                </div>
                            </div>
                    </SwiperSlide>
                    <SwiperSlide>
                        <div className={styles.slideSection}>
                            <div className={styles.proCard}>
                                <ProCard/>
                                <ProCard/>
                                <ProCard/>
                                <ProCard/>
                                <ProCard/>
                            </div>
                        </div>
                    </SwiperSlide>
                    <SwiperSlide>
                        <div className={styles.slideSection}>
                            <div className={styles.proCard}>
                                <ProCard/>
                                <ProCard/>
                                <ProCard/>
                                <ProCard/>
                                <ProCard/>
                            </div>
                        </div>
                    </SwiperSlide>
                </Swiper>
            </div>
            <button className={styles.registerPro}>
                <span className={styles.registerText}>프로 등록하기</span>
            </button>
        </div>
    )
}

export default ProCardSlider;