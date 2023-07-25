## 概要
書籍管理システムです。
書籍と著者の情報をRDBに登録・変更・検索ができるAPIです

**以下の技術が利用されている。** <br>
- Spring boot 3  (Kotlin) <br>
- Junit 5 (Kotlin) <br>
- JDK17 <br>
- Jooq 3.18.4 <br>

## Local DBの立ち上げ方
本APIがDockerのMysql 8.0を利用しています。<br>
DBのコンテーナーを実行には ./dockerディレクトリで以下のコマンドを実行する。

```bash
docker-compose up --build -d
```

コンテーナーの削除仕方

```bash
docker-compose down
```
DBにすでにデータが存在する。<br>
そのデータは　./docker/schemas/books.sql ファイルで確認できる
## Jooq ビルド

#### コマンドラインでビルド
```bash
gradlew generateJooq
```

#### Intellijでビルド
Gradleビルドしたら、Gradleのタスクの一覧にjooq/generateJooqタスクが追加される。<br>
generateJooqを実行してください。


## APIエンドポイント

### 全ての本籍を検索
全ての書籍の情報を返却する

* **URL:** _/v1/books_

* **Method:**
  `GET` 
  
* **成功レスポンス:**
  * **Code:** 200 OK <br />
  * **Body:** <br>
    
    ```json
     [
	    {
	        "bookId": 1,
	        "author": "Tolkien",
	        "title": "Hobbit"
	    },
	    {
	        "bookId": 2,
	        "author": "Tolkien",
	        "title": "Lord of the Rings"
	    },
	    {
	        "bookId": 3,
	        "author": "Homer",
	        "title": "Iliad"
	    }
     ]
    ```
    <br>
    
### 本籍検索
指定した書籍の情報を返却する

* **URL:** _/v1/book/{bookId}_

* **Method:**
  `GET` 
  
* **成功レスポンス:**
  * **Code:** 200 OK <br />
  * **Body:** <br>
    
    ```json
    {
        "bookId": 1,
        "author": "Tolkien",
        "title": "Hobbit"
    }
    ```
* **エラーレスポンス:**
  * **Code:** 400 Bad Request <br />
  * **原因:** {bookId}に数字以外を設定する <br />
  * **Body:** `{
    "message": "bookIdパラメータにIntegerを設定してください",
    "details": "uri=/v1/book/n"
}`
<br>

### 著者検索
指定した著者の情報を返却する

* **URL:** _/v1/author/{authorId}_

* **Method:**
  `GET` 
  
* **成功レスポンス:**
  * **Code:** 200 OK <br />
  * **Body:** <br>
    
    ```json
    {
	    "authorId": 1,
	    "authorName": "Tolkien",
	    "authorCountry": "UK",
	    "works": [
	        {
	            "bookId": 1,
	            "title": "Hobbit"
	        },
	        {
	            "bookId": 2,
	            "title": "Lord of the Rings"
	        }
	    ]
   } 
   ```
* **エラーレスポンス:**
  * **Code:** 400 Bad Request <br />
  * **原因:** {authorId}に数字以外を設定する <br />
  * **Body:** `{
    "message": "authorIdパラメータにIntegerを設定してください",
    "details": "uri=/v1/author/n"
}`
<br>

### 著者登録
著者をDBに追加する

* **URL:** _/v1/create/author_

* **Method:**
  `PUT` 
  
* **リクエストJSON:**
* 
```json
     {
	    "name":  "山本太郎",
	    "country": "Japan"
     }
```
  
* **成功レスポンス:**
  * **Code:** 200 OK <br />
  * **Body:** <br>
    
	```json
   	 {
	    "message": "登録に成功しました。",
	    "success": true
	 }   
	```
* **エラーレスポンス:**
  * **Code:** 400 Bad Request <br />
  * **原因:** {"name"}が空 <br />
  * **Body:** `{
	    "message": "nameを設定してください。",
	    "details": "uri=/v1/create/author"
	　}`
　<br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"country"}が空 <br />
  * **Body:** `{
    "message": "countryを設定してください。",
    "details": "uri=/v1/create/author"
　}`
　<br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"country"}が32文字より長い <br />
  * **Body:** `{
    "message": "countryは32文字以内に入力してください。",
    "details": "uri=/v1/create/author"}`
    <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"name"}が64文字より長い <br />
  * **Body:** `{
    "message": "nameは64文字以内に入力してください。",
    "details": "uri=/v1/create/author"}`
    <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** どれかのJsonキーがない時 <br />
  * **Body:** `{
    "message": "Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。",
    "details": "uri=/v1/create/author"}`

　
　<br>

### 書籍登録
書籍をDBに追加する

* **URL:** _/v1/create/book_

* **Method:**
  `PUT` 
  
* **リクエストJSON:**
* 
```json
     {
	    "title": "本タイトル",
	    "authorId": 2
     }
```
  
* **成功レスポンス:**
* **Code:** 200 OK <br />
* **Body:** <br>
    
	```json
   	 {
	    "message": "登録に成功しました。",
	    "success": true
	 }   
	```
* **エラーレスポンス:**
  * **Code:** 400 Bad Request <br />
  * **原因:** {"title"}が空 <br />
  * **Body:** `{
	    "message": "titleを設定してください。",
	    "details": "uri=/v1/create/book"
	　}`
  <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"title"}が128文字より長い <br />
  * **Body:** `{
    "message": "titleは128文字以内に入力してください。",
    "details": "uri=/v1/create/book"
　}`
<br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"authorId"}が文字 (数字に変換できない) <br />
  * **Body:** `{
    "message": "Objectパラメターのフィルドのデータ型に誤りがあります。",
    "details": "uri=/v1/create/book"
}`
<br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"authorId"}が負数 <br />
  * **Body:** `{
    "message": "authorIDの値には、1以上の数字を入力してください。",
    "details": "uri=/v1/create/book"}`
<br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** どれかのJsonキーがない時 <br />
  * **Body:** `{
    "message": "Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。",
    "details": "uri=/v1/create/book"}`
    
    <br>
    
    
### 著者更新
著者を更新する
リクエストJSONの設定されているキーのみDBを更新する。
設定されていないキーがDBのデータを上書きしない。

* **URL:** _/v1/update/author_

* **Method:**
  `POST` 
  
* **リクエストJSON:**
* 
```json
     {
        "authorId": 2,
        "name":  "山本太郎",
        "country": "USA"
     }
```
  
* **成功レスポンス:**
  * **Code:** 200 OK <br />
  * **Body:** <br>
    
	```json
   	 {
	    "message": "更新に成功しました。",
	    "success": true
	 }   
	```
	
	著者id (authorId)がDBに存在しない場合、以下のレスポンスになる
	
	```json
   	 {
	    "message": "更新に失敗しました。",
	    "success": false
	 }
	```
* **エラーレスポンス:**
  * **Code:** 400 Bad Request <br />
  * **原因:** {"country"}が32文字より長い <br />
  * **Body:** `{
    "message": "countryは32文字以内に入力してください。",
    "details": "uri=/v1/update/author"}`
    <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"name"}が64文字より長い <br />
  * **Body:** `{
    "message": "nameは64文字以内に入力してください。",
    "details": "uri=/v1/update/author"}`
    <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** どれかのJsonキーがない時 <br />
  * **Body:** `{
    "message": "Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。",
    "details": "uri=/v1/update/author"}`
    <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"authorId"}が文字 (数字に変換できない) <br />
  * **Body:** `{
    "message": "Objectパラメターのフィルドのデータ型に誤りがあります。",
    "details": "uri=/v1/update/author"}`
    <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** どれかのJsonキーがない時 <br />
  * **Body:** `{
    "message": "Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。",
    "details": "uri=/v1/update/author"}`

    <br>
    
    
    ### 書籍更新
書籍を更新する
リクエストJSONの設定されているキーのみDBを更新する。
設定されていないキーがDBのデータを上書きしない。

* **URL:** _/v1/update/book_

* **Method:**
  `POST` 
  
* **リクエストJSON:**
* 
```json
     {
	    "bookId": 1,
	    "title": "タイトル",
	    "authorId": 3
     }
```
  
* **成功レスポンス:**
* **Code:** 200 OK <br />
* **Body:** <br>
    
	```json
   	 {
	    "message": "更新に成功しました。",
	    "success": true
	 }   
	```
	書籍id (bookId)がDBに存在しない場合、以下のレスポンスになる
	
	```json
   	 {
	    "message": "更新に失敗しました。",
	    "success": false
	 }
	```
	
	設定した著者id (authorId)がDBに存在しない場合、以下のレスポンスになる
	
	```json
   	 {
	    "message": "DBに存在しない著者で書籍の情報を更新することができません、先に著者を登録してください。",
	    "success": false
	 }
	```
* **エラーレスポンス:**
  * **Code:** 400 Bad Request <br />
  * **原因:** {"title"}が128文字より長い <br />
  * **Body:** `{
    "message": "titleは128文字以内に入力してください。",
    "details": "uri=/v1/update/book"
　}`
<br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"authorId"}が文字 (数字に変換できない) <br />
  * **Body:** `{
    "message": "Objectパラメターのフィルドのデータ型に誤りがあります。",
    "details": "uri=/v1/update/book"
}`
<br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"authorId"}が負数 <br />
  * **Body:** `{
    "message": "authorIdの値には、1以上の数字を入力してください。",
    "details": "uri=/v1/update/book"}`
 <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"bookId"}が文字 (数字に変換できない) <br />
  * **Body:** `{
    "message": "Objectパラメターのフィルドのデータ型に誤りがあります。",
    "details": "uri=/v1/update/book"}`
<br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** {"bookId"}が負数  <br />
  * **Body:** `{
    "message": "bookIdの値には、1以上の数字を入力してください。",
    "details": "uri=/v1/update/book"}`
    <br/><br/>
  * **Code:** 400 Bad Request <br />
  * **原因:** どれかのJsonキーがない時 <br />
  * **Body:** `{
    "message": "Objectパラメターの必要なフィルドが設定されなかったため、Jsonリクエストがパースできませんでした。",
    "details": "uri=/v1/update/book"}`
    
    <br>