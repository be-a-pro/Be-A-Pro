import axios from "axios";

export default function UseOpenGraph() {
  async function onClickOpenGraph () {

    // 네이버는 테스트를 완료했어요!
    /*
    console.log(
      result.data.split("<meta").filter((el: string) => el.includes("og:title")) // 요런식으로 끄내면 된다잉
    ); */
    try {

      const result = await axios.get("/?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%ED%85%8C%EC%8A%A4%ED%8A%B8");
      console.log(result.data);

      const resultOfWeb = await axios.get("/klmhyeonwoo/H4VEBEAUTY_BRANDING_PAGE");
      console.log(resultOfWeb);
  
      const resultOfGoogle = await axios.get("/forms/d/1BjxWWSKOcco8-oTSQJe4KyU9JBjKTomyvclZgTjsZs8/edit?pli=1");
      console.log(resultOfGoogle);
    }
    catch {
      console.log('error!');
    }
  };

  return (
    <div>
      <h1>사이트 미리보기 연습</h1>
      <button onClick={onClickOpenGraph}>미리보기 실행</button>
    </div>
  );
}