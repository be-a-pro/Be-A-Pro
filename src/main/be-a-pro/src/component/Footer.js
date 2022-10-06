import styles from './Footer.module.css';
import { Link } from 'react-router-dom';
import logo from '../images/be-a-pro-w.svg';

function Footer() {
    return (
        <>
        <footer className={styles.footer}>
            <div className={styles.container}>
                <nav className={styles.navigator}>
                    <Link to="#">
                        <span className={styles.navSpan}>
                            공지사항
                        </span>
                    </Link>
                    <Link to="#">
                        <span className={styles.navSpan}>
                            이용약관
                        </span>
                    </Link>
                    <Link to="#">
                        <span className={styles.navSpan}>
                            회원약관
                        </span>
                    </Link>
                    <Link to="#">
                        <span className={styles.navSpan}>
                            개인정보처리방침
                        </span>
                    </Link>
                    <Link to="#">
                        <span className={styles.navSpan}>
                            인스타그램
                        </span>
                    </Link>
                    <Link to="#">
                        <span className={styles.navSpan}>
                            블로그
                        </span>
                    </Link>
                </nav>
                <div className={styles.splitLine}></div>
                <div className={styles.beerProfile}>
                    <img src={logo}></img>
                    <div className={styles.beerInformation}>
                        <div className={styles.beerProfileDetail}>
                            <span>비어프로</span>
                            <span>|</span>
                            <span>사업자등록번호 : 000-00-00000</span>
                            <span>|</span>
                            <span>대표 : 이동규</span>
                            <span>|</span>
                            <span>통신판매신고번호 : 제 0000 - 서울마포 - 0000호</span>
                        </div>
                        <div className={styles.beerProfileDetail}>
                            <span>주소 : 서울시 땡땡구 땡땡동 00-00 가나다라마바 0000호</span>
                            <span>|</span>
                            <span>TEL : 000.0000.00000</span>
                            <span>|</span>
                            <span>개인정보담당자 : 송유진 help@beapro.co.kr</span>
                        </div>
                    </div>
                    <span className={styles.copyright}>Copyright ⓒ 2022 beapro. All rights reserved.</span>
                </div>
            </div>
        </footer>
        </>
    )
}

export default Footer;