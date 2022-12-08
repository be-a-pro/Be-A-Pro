import axios from "axios";
import styles from './UseOpenGraph.module.scss';
import profile from '../images/project/profile-img.png';
import { useState } from "react";
import { useEffect } from "react";

export default function UseOpenGraph() {

  const [state, setState] = useState(false);
  const [ogTitle, setTitle] = useState('');
  const [ogContent, setContent] = useState('');
  const [ogImages, setImages] = useState('');
  const [ogUrl, setUrl] = useState('');

  useEffect(() => {
    onClickOpenGraph();
  }, [])

  async function onClickOpenGraph() {
    /* console.log(
      result.data.split("<meta").filter((el: string) => el.includes("og:title")) // 요런식으로 끄내면 된다잉
    ); */
    try {
      const result = await axios.get("https://www.wanted.co.kr/community/post/7351");

      const title = result.data.split("<meta").filter((item) => item.includes("og:title"));
      const content = result.data.split("<meta").filter((item) => item.includes("og:description"));
      const images = result.data.split("<meta").filter((item) => item.includes("og:image"));
      const url = result.data.split("<meta").filter((item) => item.includes("og:url"));

      const scaledTitle = title[0].slice(title[0].indexOf(`content="`) + `content=`.length, title[0].indexOf(`/>`));
      const scaledcontent = content[0].slice(content[0].indexOf(`content="`) + `content="`.length, content[0].indexOf(`"/>`));
      const scaledImages = images[0].slice(images[0].indexOf(`content="`) + `content="`.length, images[0].indexOf(`"/>`));
      const scaledUrl = url[0].slice(url[0].indexOf(`content="`) + `content="`.length, url[0].indexOf(`/>`)).replace(`"`, '');

      // console.log(scaledUrl)
      setState(true)
      setTitle(() => setTitle(scaledTitle));
      setContent(() => setContent(scaledcontent.slice(0, 50) + ` ...`));
      setUrl(() => setUrl(scaledUrl.slice(0, 80) + ` ...`));
      setImages(() => setImages(scaledImages));

      console.log(scaledUrl)
      // const resultOfWeb = await axios.get("/klmhyeonwoo/H4VEBEAUTY_BRANDING_PAGE");
      // console.log(resultOfWeb);

      // const resultOfGoogle = await axios.get("/forms/d/1BjxWWSKOcco8-oTSQJe4KyU9JBjKTomyvclZgTjsZs8/edit?pli=1");
      // console.log(resultOfGoogle);
    }
    catch (error) {
      console.log('error!', error);
    }
  };


  if (!ogTitle) {
    return <p className={styles.loading}>정보를 불러오고있어요 👀</p>
  }

  return (
    <>
      <a href={ogUrl} target="_blank" >
        <pre className={styles.urlOfGoogleForm}> {ogUrl} </pre>
        <div className={styles.boxOfGoogleForm}>
          <img className={styles.boxOfImage} src={ogImages} />
          <div className={styles.boxOfContents}>
            <span className={styles.boxTitle}>{ogTitle}</span> <br />
            <span className={styles.boxContent}>{ogContent}</span>
          </div>
          <div className={styles.boxOfUser}>
            <img src={profile} />
            <div className={styles.boxOfLink}>
              {ogUrl}
            </div>
          </div>
        </div>
      </a>
      {/* 
      <h1>사이트 미리보기 연습</h1>
      <button onClick={onClickOpenGraph}>미리보기 실행</button> */}
    </>
  );
}