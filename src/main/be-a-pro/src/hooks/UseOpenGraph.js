
import axios from "axios";

export default function UseOpenGraph() {

    function test() {
        axios.get('/search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%E3%85%8D%E3%85%87%E3%84%B4%E3%85%81%E3%85%8D%E3%84%B4%E3%85%81')
             .then(data => {
               console.log(data.data)
             })
      }

  const onClickOpenGraph = async () => {

    const result = await axios.get("/search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=%E3%85%8D%E3%85%87%E3%84%B4%E3%85%81%E3%85%8D%E3%84%B4%E3%85%81");
    console.log(result);    
    /*
    console.log(
      result.data.split("<meta").filter((el: string) => el.includes("og:title")) // 요런식으로 끄내면 된다잉
    );
    */
  };
  return (
    <div>
      <h1>사이트 미리보기 연습</h1>
      <button onClick={test}>미리보기 실행</button>
      <button onClick={onClickOpenGraph}>미리보기 실행</button>
    </div>
  );
}