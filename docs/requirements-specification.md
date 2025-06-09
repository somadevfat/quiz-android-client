# クイズアプリケーション要件定義書 v1.0

**プロジェクト**: Quiz Android Client (Kotlin + Jetpack Compose)  
**バックエンド**: quiz-api (Spring Boot)  
**作成日**: 2025年6月9日  
**技術スタック**: Kotlin, Jetpack Compose, Clean Architecture

---

## 📋 プロジェクト概要

### 目的
quiz-app-contentfulアプリケーションの完全なKotlin + Jetpack Compose移行版を開発し、quiz-apiバックエンドとの統合により、モダンなクイズ学習プラットフォームを構築する。

### 技術制約
- **フロントエンド**: Kotlin + Jetpack Compose のみ
- **バックエンド**: quiz-api (Spring Boot) - 現在問題データのみ実装済み
- **アーキテクチャ**: Clean Architecture + MVVM
- **デザイン**: 機能要件に集中、デザイン仕様は除外

---

## 🎯 機能要件

### 1. 認証・ユーザー管理機能

#### 1.1 ユーザー認証
- **ログイン機能**
  - ユーザー名・パスワードによる認証
  - JWT トークンベース認証
  - 自動ログイン（トークン保持）
  - セキュアなローカルストレージ（EncryptedSharedPreferences）

- **ユーザー登録機能**
  - 新規アカウント作成
  - ユーザー名、メール、パスワード入力
  - パスワード確認・バリデーション
  - 入力データ検証（最小6文字パスワード等）

- **認証状態管理**
  - 認証状態の永続化
  - 自動トークンリフレッシュ
  - 認証エラーハンドリング
  - ログアウト機能

#### 1.2 セッション管理
- **トークン管理**
  - JWT アクセストークン・リフレッシュトークン
  - トークン暗号化保存
  - 期限切れ自動更新
  - 不正トークン検知・処理

### 2. クイズ管理・表示機能

#### 2.1 クイズ一覧表示
- **基本表示機能**
  - 全クイズ一覧の取得・表示
  - カード型UI表示
  - カテゴリ別フィルタリング
  - 検索機能（クイズタイトル・内容）

- **クイズ情報表示**
  - クイズタイトル・説明
  - 難易度表示（初級・中級・上級）
  - 章・カテゴリ分類
  - 問題数・推定時間
  - 作成・更新日時

#### 2.2 クイズ詳細機能
- **詳細情報表示**
  - 包括的なクイズ情報
  - 前提知識・学習目標
  - 関連クイズの推奨
  - 問題プレビュー

### 3. クイズ実行・回答機能

#### 3.1 問題表示・回答
- **問題表示**
  - 一問一答形式での表示
  - 複数選択肢（Multiple Choice）
  - プログラムコード表示対応
  - 問題文の適切なフォーマット

- **回答機能**
  - 選択肢からの回答選択
  - 回答確定・送信
  - 回答修正（送信前）
  - プログレスバー・進捗表示

#### 3.2 即座フィードバック・解説
- **回答フィードバック**
  - 正解・不正解の即時表示
  - カラー区別（正解：緑、不正解：赤）
  - 正解選択肢のハイライト
  - 回答理由の表示

- **解説機能**
  - 詳細な解答解説
  - 技術的な背景説明
  - 関連概念の補足
  - 参考リンク・追加学習資料

#### 3.3 クイズセッション管理
- **進行管理**
  - 問題間ナビゲーション（前・次）
  - 現在問題位置表示
  - 全体進捗率計算
  - セッション中断・再開

- **スコア・結果管理**
  - リアルタイムスコア計算
  - 最終スコア表示
  - 正答率・所要時間
  - 結果保存・履歴記録

### 4. 学習進捗・履歴管理

#### 4.1 ブックマーク機能
- **問題保存**
  - 重要問題のマーク機能
  - ローカル保存・同期
  - ブックマーク一覧表示
  - カテゴリ別整理

- **管理機能**
  - ブックマーク追加・削除
  - 一括操作（複数選択）
  - 検索・フィルタリング
  - エクスポート機能

#### 4.2 学習履歴追跡
- **履歴記録**
  - 解答履歴の詳細記録
  - 時系列での表示
  - 問題別アクセス回数
  - 学習時間の追跡

- **分析機能**
  - 学習パターン分析
  - 弱点分野の特定
  - 改善推奨事項
  - 学習計画提案

#### 4.3 統計・分析機能
- **学習統計**
  - 問題別正答率・解答回数
  - 分野別・章別パフォーマンス
  - 時期別成績推移
  - 難易度別分析

- **詳細分析**
  - 学習ストリーク（連続日数）
  - 1日の学習時間・問題数
  - 成長曲線・トレンド
  - 目標達成率

### 5. カテゴリ・章構成管理

#### 5.1 構造化学習
- **学習単位管理**
  - 6ユニット構成対応
  - 章・セクション階層
  - レッスン別整理
  - 学習順序の推奨

- **進捗管理**
  - ユニット別完了状況
  - 章別進捗率表示
  - 未完了分野の明示
  - 次の学習推奨

#### 5.2 分野別機能
- **カテゴリ表示**
  - Java基礎、データ型、制御構造
  - クラス設計、継承、例外処理
  - カテゴリ別アイコン・色分け
  - 難易度レベル表示

### 6. 設定・環境管理

#### 6.1 アプリケーション設定
- **ユーザー設定**
  - プロフィール管理
  - 学習設定（通知、リマインダー）
  - データ同期設定
  - プライバシー設定

- **アプリケーション情報**
  - バージョン情報
  - 利用規約・プライバシーポリシー
  - サポート情報
  - フィードバック機能

#### 6.2 データ管理
- **ローカルデータ**
  - オフライン学習対応
  - データキャッシュ管理
  - 容量管理・クリーンアップ
  - データ同期状態表示

### 7. ナビゲーション・UI構造

#### 7.1 ナビゲーション
- **画面遷移パターン**
  - 起動時認証チェック
  - ログイン → クイズ一覧
  - 一覧 → クイズ実行
  - 実行 → 結果 → 一覧

- **Navigation Component**
  - Fragment間シームレス遷移
  - Deep Link対応
  - Back Stack適切管理
  - 画面状態保持

#### 7.2 UI構造（Jetpack Compose）
- **レイアウト構成**
  - Material 3 Design準拠
  - レスポンシブデザイン
  - ダークテーマ対応
  - アクセシビリティ配慮

---

## 🏗️ システムアーキテクチャ要件

### 1. クライアントサイドアーキテクチャ

#### 1.1 Clean Architecture構成
```
├── presentation/        # UI層 (Compose + ViewModel)
├── domain/             # ビジネスロジック層
├── data/              # データアクセス層 (Repository + API)
└── di/                # 依存性注入 (Hilt)
```

#### 1.2 MVVM + Repository Pattern
- **ViewModel**: UI状態管理・ビジネスロジック
- **Repository**: データソース抽象化
- **UseCase**: 複雑なビジネスロジック分離

### 2. データフロー要件

#### 2.1 状態管理
- **StateFlow**: リアクティブ状態管理
- **UiState**: 画面状態の一元管理
- **Loading/Success/Error**: 一貫したエラーハンドリング

#### 2.2 非同期処理
- **Kotlin Coroutines**: 非同期処理の主要手段
- **Flow**: リアクティブデータストリーム
- **Dispatcher**: 適切なスレッド分離

### 3. データモデル要件

#### 3.1 Domain Models
```kotlin
data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val difficulty: QuizDifficulty,
    val categoryId: String,
    val categoryName: String,
    val questionCount: Int,
    val timeLimit: Int?,
    val imageUrl: String?,
    val createdAt: String,
    val updatedAt: String
)

data class Question(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val code: String? = null
)

enum class QuizDifficulty { BEGINNER, INTERMEDIATE, ADVANCED }
```

#### 3.2 UI State Models
```kotlin
data class QuizListUiState(
    val isLoading: Boolean = false,
    val quizzes: List<Quiz> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedCategory: String? = null
)

data class QuizUiState(
    val isLoading: Boolean = false,
    val currentQuestion: Question? = null,
    val currentQuestionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val score: Int = 0,
    val timeRemaining: Int? = null,
    val error: String? = null
)
```

---

## 🔌 API統合要件

### 1. 認証API要件

#### 1.1 必要なエンドポイント
```
POST /auth/login          # ログイン
POST /auth/register       # 新規登録
POST /auth/logout         # ログアウト
GET  /auth/me            # 現在ユーザー取得
POST /auth/refresh       # トークン更新
```

#### 1.2 認証データ形式
```kotlin
data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val accessToken: String, val refreshToken: String)
data class RegisterRequest(val username: String, val email: String, val password: String)
```

### 2. クイズAPI要件（quiz-api拡張）

#### 2.1 現在実装済み
```
GET /api/v1/quizzes       # クイズ一覧取得
GET /api/v1/quizzes/{id}  # 個別クイズ取得
```

#### 2.2 追加実装必要
```
GET /api/v1/quizzes/search?q={query}           # クイズ検索
GET /api/v1/quizzes/category/{categoryId}      # カテゴリ別クイズ
GET /api/v1/quizzes/{id}/questions             # クイズ問題詳細
POST /api/v1/quizzes/{id}/sessions             # クイズセッション開始
PUT /api/v1/quizzes/sessions/{sessionId}/answer # 回答送信
GET /api/v1/quizzes/sessions/{sessionId}/result # 結果取得
```

### 3. ユーザーデータAPI要件
```
GET /api/v1/users/me/bookmarks                 # ブックマーク一覧
POST /api/v1/users/me/bookmarks                # ブックマーク追加
DELETE /api/v1/users/me/bookmarks/{questionId} # ブックマーク削除
GET /api/v1/users/me/history                   # 学習履歴
GET /api/v1/users/me/statistics                # 学習統計
```

---

## 🧪 品質要件

### 1. テスト要件

#### 1.1 テスト戦略
- **Unit Tests**: 70% (ViewModel, Repository, Domain)
- **Integration Tests**: 20% (API統合、E2Eフロー)
- **UI Tests**: 10% (Compose UI、ユーザーシナリオ)

#### 1.2 必須テストケース
- **認証フロー**: ログイン・登録・ログアウト
- **クイズ実行**: 問題表示・回答・結果表示
- **データ同期**: オンライン・オフライン切り替え
- **エラーハンドリング**: ネットワークエラー・認証エラー

### 2. パフォーマンス要件

#### 2.1 応答時間
- **アプリ起動**: 3秒以内
- **画面遷移**: 1秒以内
- **API レスポンス**: 2秒以内
- **オフライン表示**: 即座

#### 2.2 リソース使用量
- **メモリ使用量**: 100MB以下（通常時）
- **ストレージ**: 50MB以下（キャッシュ除く）
- **バッテリー**: 効率的な電力使用

### 3. セキュリティ要件

#### 3.1 認証・認可セキュリティ
- **JWT トークンセキュリティ**
  - アクセストークン有効期限: 15分
  - リフレッシュトークン有効期限: 7日
  - トークンローテーション実装
  - 署名検証・改ざん検知
  - Secure Storage使用 (EncryptedSharedPreferences)

- **パスワードセキュリティ**
  - 最小8文字、大小英数字・記号組み合わせ必須
  - bcrypt/scrypt使用によるハッシュ化
  - パスワード履歴管理（過去5回分重複禁止）
  - ブルートフォース攻撃対策（試行回数制限）
  - パスワードリセット機能（メール認証）

- **多要素認証**
  - SMS/Email OTP対応
  - TOTP（Google Authenticator）対応
  - バイオメトリクス認証（指紋・顔認証）
  - デバイス登録・管理機能

#### 3.2 通信セキュリティ
- **暗号化通信**
  - TLS 1.3 最小バージョン
  - Certificate Pinning実装
  - Public Key Pinning実装
  - HSTS (HTTP Strict Transport Security)

- **API セキュリティ**
  - Rate Limiting (60 requests/minute)
  - CORS適切設定
  - CSRFトークン実装
  - API Key管理・ローテーション
  - Request/Response暗号化

#### 3.3 データ保護・プライバシー
- **ローカルデータ保護**
  - AES-256暗号化（Room Database）
  - Keystore使用による鍵管理
  - アプリケーション署名検証
  - Root/Jailbreak検出
  - デバッグ検出・無効化

- **個人情報保護**
  - GDPR準拠データ処理
  - データ最小化原則
  - 明示的同意取得
  - データポータビリティ対応
  - 忘れられる権利対応

- **データ流出対策**
  - ログ出力時個人情報マスキング
  - スクリーンショット防止
  - アプリバックグラウンド時コンテンツ隠蔽
  - メモリダンプ対策

#### 3.4 アプリケーションセキュリティ
- **コード保護**
  - ProGuard/R8難読化
  - Native Code保護
  - Anti-Tampering対策
  - 静的解析対策
  - 動的解析対策

- **実行時保護**
  - Frida検出・ブロック
  - Xposed検出・ブロック
  - エミュレータ検出
  - Hook検出・防止
  - メモリ改ざん検出

#### 3.5 入力検証・サニタイゼーション
- **クライアントサイド検証**
  - リアルタイム入力バリデーション
  - XSS対策（HTML エスケープ）
  - SQLインジェクション対策
  - Path Traversal対策
  - Command Injection対策

- **サーバーサイド検証**
  - 二重検証実装（クライアント + サーバー）
  - ホワイトリスト方式採用
  - 入力長制限・文字種制限
  - ファイルアップロード検証
  - Content-Type検証

#### 3.6 ログ・監査
- **セキュリティログ**
  - 認証成功・失敗ログ
  - 異常アクセスパターン検知
  - API不正利用検知
  - データアクセスログ
  - 設定変更ログ

- **監査・分析**
  - リアルタイム異常検知
  - セキュリティインシデント対応
  - フォレンジック対応準備
  - コンプライアンス監査対応

#### 3.7 セキュリティテスト
- **脆弱性テスト**
  - OWASP Mobile Top 10準拠
  - 静的コード解析（SonarQube/Veracode）
  - 動的解析・ペネトレーションテスト
  - 依存関係脆弱性スキャン
  - ファジングテスト

- **継続的セキュリティ**
  - CI/CDパイプライン組み込み
  - 自動脆弱性スキャン
  - セキュリティパッチ管理
  - 定期セキュリティ監査

#### 3.8 インシデント対応
- **検知・対応**
  - 異常行動検知アラート
  - 自動ブロック・隔離機能
  - インシデントレスポンス計画
  - データ侵害通知体制
  - 復旧・事業継続計画

- **コンプライアンス**
  - GDPR準拠体制
  - 個人情報保護法対応
  - SOC2 Type II対応
  - ISO27001準拠
  - PCI DSS準拠（決済機能追加時）

---

## 📱 デバイス・プラットフォーム要件

### 1. 対応Android仕様
- **最小SDK**: Android 7.0 (API 24)
- **対象SDK**: Android 14 (API 34)
- **アーキテクチャ**: arm64-v8a, armeabi-v7a, x86_64

### 2. デバイス対応
- **画面サイズ**: 5.0〜12.9インチ
- **解像度**: HDPI〜XXXHDPI対応
- **メモリ**: 3GB以上推奨
- **ストレージ**: 1GB以上の空き容量

---

## 🚀 段階的実装計画

### Phase 1: コア機能実装 (優先度: 高)
1. **認証システム完成**
2. **クイズ一覧表示**
3. **基本的なクイズ実行機能**
4. **quiz-api統合**

### Phase 2: 学習機能拡張 (優先度: 中)
1. **ブックマーク機能**
2. **学習履歴・統計**
3. **オフライン対応**
4. **検索・フィルタ機能**

### Phase 3: 高度機能・最適化 (優先度: 低)
1. **詳細分析機能**
2. **学習推奨システム**
3. **パフォーマンス最適化**
4. **アクセシビリティ向上**

---

## 📋 非機能要件

### 1. 保守性
- **コード品質**: Clean Architecture厳守
- **テストカバレッジ**: 80%以上
- **ドキュメント**: 包括的な技術文書
- **依存関係**: 最新ライブラリ使用

### 2. 拡張性
- **モジュラー設計**: 機能別モジュール分離
- **プラグイン対応**: 新機能追加容易性
- **API バージョニング**: 後方互換性確保

### 3. 運用性
- **ログ機能**: 包括的なログ記録
- **監視機能**: クラッシュレポート
- **デバッグ支援**: 開発者向けツール
- **パフォーマンス計測**: APM統合

---

**この要件定義書は、quiz-app-contentfulの全機能をKotlin + Jetpack Composeで再実装し、quiz-apiバックエンドと統合するための包括的な仕様書です。**