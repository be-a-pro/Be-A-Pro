import axios from "axios";

export default function OpenGraphPreviewPage() {
  const onClickOpenGraph = async () => {
    const result = await axios.get("https://www.gmarket.co.kr");
    console.log(
      result.data.split("<meta").filter((el: string) => el.includes("og:title")) // 요런식으로 끄내면 된다잉
    );
  };
  return (
    <div>
      <h1>사이트 미리보기 연습</h1>
      <button onClick={onClickOpenGraph}>미리보기 실행</button>
    </div>
  );
}