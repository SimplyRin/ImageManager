# ImageManager
ダウンロードフォルダ、片付いてなくない？うぉうぉう。

でもね、ImageManager 自体が散らかってきたんだ...

めんどくさいから使いたかったらビルドもしてください。

Twitter でダウンロードした JPG, PNG, ZIP ファイルなどを別の指定したフォルダにコピーさせます。

ダウンロードフォルダが Twitter の画像などで埋まる方におすすめかもしれません。

ファイル名から Twitter からダウンロードしたファイルかどうか確認するため、動作させるには以下の拡張機能が必要です。

- [Twitter, Download Original Image](https://chrome.google.com/webstore/detail/twitter-download-original/oohidjkamhoccdjfhokgjcefajmfbgep)

- [Twitter メディアダウンローダ](https://chrome.google.com/webstore/detail/twitter-media-downloader/cblpjenafgeohmnjknfhpdbdljfkndig)

# Requiresments
- [7-Zip](https://sevenzip.osdn.jp/)

# Configuration
- `USERNAME` : 使用している PC のユーザー名に置き換わります。
- `Loop` : ダウンロードフォルダをスキャンするタイミングです。 (初期値 `60` 秒ごと)
- `OnCopied` : コピー時に元のファイルを削除します。
- `Zip_Type` は現在以下のオプションが利用可能です。
  - `default`: ZIP 名のフォルダを作成してその中にファイルを展開します。
  - `each`: 画像(Images)フォルダに ZIP の中ファイルを展開します。
  - `raw`: ZIP フォルダにファイルを展開します。
  - `username`: ZIP フォルダ名からユーザーネームを取得、フォルダを作成しその中にファイルを展開します。 (おすすめ)
- `SkipProfileDownload` : true にすると、[Twitter メディアダウンローダ](https://chrome.google.com/webstore/detail/twitter-media-downloader/cblpjenafgeohmnjknfhpdbdljfkndig) でダウンロードしたファイルは、EACH, RAW にコピーしなくなります。
- `DeleteArchiveAfterStarting` : 起動時に Zip_Archive フォルダを削除します。
```Json
{
	"7z.exe": "C:/Program Files/7-Zip/7z.exe",
	"Loop": 60,
	"Downloads": "C:/Users/%USERNAME/Downloads",
	"Images": "C:/Users/%USERNAME/Pictures/ImageManager/Images",
	"MP4": "C:/Users/%USERNAME/Pictures/ImageManager/MP4",
	"Zip": "C:/Users/%USERNAME/Pictures/ImageManager/Zip",
	"OnCopied": "delete",
	"Zip_Type": "default",
	"Zip_Archive": "C:/Users/%USERNAME/Pictures/ImageManager/Zip/Archive",
	"Unknown": "C:/Users/%USERNAME/Pictures/ImageManager/Unknown",
	"SkipProfileDownload": true,
	"DeleteArchiveAfterStarting": false
}
```

# Setup
- 1: [ここから](https://github.com/SimplyRin/ImageManager/releases) 最新の ImageManager ファイルをダウンロード

- 2: ダウンロードした ImageManager をどこかにコピー
  - C:\Users\Name\Tasks\ImageManager-1.0.jar など...

- 3: ImageManager-1.0.jar を起動
  - 生成された config.yml をお好みに編集してください。
  - コードを書くのがめんどくさかったので編集後はタスクマネージャーで Java プロセスを停止させてください。

- 4: 2 でコピーした ImageManager のショートカットを次のディレクトリに作成
  - %USERPROFILE%\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup\ImageManager.lnk

- 5: 最後にまた起動したら完了
