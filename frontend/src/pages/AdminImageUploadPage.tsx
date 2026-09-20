import { type ChangeEvent, useEffect, useState } from "react";
import { Navigate } from "react-router-dom";
import api from "../api/axios";
import type { CurrentUser } from "../types/auth";

function AdminImageUploadPage() {
  const token = localStorage.getItem("accessToken");
  const [authorized, setAuthorized] = useState<boolean | null>(null);
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (!token) { setAuthorized(false); return; }
    api.get<CurrentUser>("/api/users/me").then(({ data }) => setAuthorized(data.role === "ADMIN")).catch(() => setAuthorized(false));
  }, [token]);

  if (!token) return <Navigate to="/login" replace />;
  if (authorized === false) return <Navigate to="/" replace />;

  const selectFile = (event: ChangeEvent<HTMLInputElement>) => {
    const selected = event.target.files?.[0] ?? null;
    setFile(selected); setImageUrl(""); setMessage("");
    if (preview) URL.revokeObjectURL(preview);
    setPreview(selected ? URL.createObjectURL(selected) : "");
  };

  const upload = async () => {
    if (!file) { setMessage("이미지 파일을 선택해 주세요."); return; }
    const formData = new FormData(); formData.append("file", file);
    try {
      const { data } = await api.post<{ imageUrl: string }>("/api/admin/images", formData, { headers: { "Content-Type": "multipart/form-data" } });
      setImageUrl(data.imageUrl); setMessage("S3 업로드가 완료되었습니다. 아래 주소를 상품 수정 화면의 이미지 URL에 붙여 넣으세요.");
    } catch { setMessage("업로드하지 못했습니다. S3 버킷과 EC2 권한을 확인해 주세요."); }
  };

  return <section className="auth-page"><div className="auth-card image-upload-card"><span className="eyebrow">S3 IMAGE</span><h1>상품 사진 업로드</h1><p className="auth-description">JPG, PNG, WEBP 파일을 최대 5MB까지 업로드할 수 있습니다.</p><input type="file" accept="image/jpeg,image/png,image/webp" onChange={selectFile}/>{preview && <img className="upload-preview" src={preview} alt="업로드 미리보기"/>}<button className="submit-button" type="button" onClick={() => void upload()}>S3에 업로드</button>{message && <p className="form-message">{message}</p>}{imageUrl && <label>상품 이미지 URL<input value={imageUrl} readOnly onFocus={(event) => event.currentTarget.select()}/></label>}</div></section>;
}

export default AdminImageUploadPage;
