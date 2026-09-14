import "./SecurityStatus.css";

const securityItems = [
  ["BCrypt 비밀번호 암호화", "회원 비밀번호를 복호화할 수 없는 단방향 해시로 저장합니다."],
  ["JWT 사용자 인증", "로그인 성공 시 발급한 토큰으로 보호된 API 요청을 인증합니다."],
  ["관리자 접근 권한 제어", "ADMIN 권한이 있는 사용자만 상품 관리 API에 접근할 수 있습니다."],
  ["중요 정보 환경변수 관리", "DB 비밀번호와 JWT 비밀키를 소스코드와 분리해 관리합니다."],
];

function SecurityStatus() {
  return <section className="security-status" aria-labelledby="security-status-title">
    <div className="security-heading"><div className="security-icon">✓</div><div><span>SECURITY STATUS</span><h2 id="security-status-title">보안 적용 현황</h2></div><b>정상 적용</b></div>
    <div className="security-grid">{securityItems.map(([title, description]) => <article key={title}><span>✓</span><div><h3>{title}</h3><p>{description}</p></div></article>)}</div>
    <p className="security-note">보안을 위해 실제 비밀번호, 암호화 해시 전체, DB 접속정보 및 JWT 비밀키는 화면에 표시하지 않습니다.</p>
  </section>;
}

export default SecurityStatus;
