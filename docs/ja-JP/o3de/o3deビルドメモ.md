# o3deメモ

Project Sansaでは、ゲームエンジンとして Open 3D Engine (o3de) を使用します。  
以下は、利用可能にするまでの、簡単なメモです。  
なお、実行は Windows 11 Pro で行っています。  
ここでの手順は、GitHubからForkせずにCloneするやり方になっています。  
本来の手順は以下に詳細ドキュメントがありますので、必要に応じて参照してください。
https://o3de.org/docs/welcome-guide/setup/setup-from-github/

# ツール類のインストール
- Python, cmake関連  
Visual Studio 2019 Community インストール時に、以下を選択してください。  
ワークロード  
☑ Python 開発  
☑ C++ によるデスクトップ開発  
☑ C++ によるモバイル開発  
☑ C++ によるゲーム開発  
- Git関連  
通常の Git 以外に、Git Large File Storage (Git LFS) が必要との事なので、  
https://git-lfs.github.com/ からインストールしてください。  
Windows ターミナル（管理者用）を起動し、以下を実行します。  
```
git lfs install
```
GitHub接続時に毎回聞かれないよう資格情報を登録しておいてください。  

# o3de のダウンロード
以下のバッチを実行してください。 
src/GameEngine/ 内   
```
1_o3de_download.bat
```
c:/o3de/ にダウンロードされます。  
2回目以降はpullします。

# o3de のビルド
スタートメニューから以下を起動します。
```
Visual Studio 2019
  x64 Native Tools Command Prompt for VS 2019
```

ゲームエンジンおよびエディタとアセットビルダのビルドを行います。  
起動したコマンドプロンプトに以下のバッチをドラッグして実行してください。   
src/GameEngine/ 内   
```
2_o3de_build_engine.bat
```

エラーがなければ、先ほどと同様に以下のバッチをドラッグして実行してエンジンを登録します。  
src/GameEngine/ 内
```
3_o3de_register_engine.bat
```

エラーがある場合は、c:\o3deフォルダ、C:\ユーザー\{Account}\.o3deフォルダを削除して最初からやり直してください。  
成功すれば、以下のファイルができているので、実行してみましょう。  
c:\o3de\o3de\build\windows_vs2019\bin\profile\o3de.exe  

先ほどと同様に以下のバッチをドラッグして実行してください。  
（cmakeへのパスが通っていれば直接でもOKです）
```
4_o3de_run.bat
```

# 付属プロジェクト のビルドと実行
o3deを起動したらプロジェクト「AutomatedTesting」の「Build Project」から「Build Now」を実行します。  
※かなり時間が掛かります。  
ビルドが成功したら「Open Editor」でEditorを起動します。
※初回かなり時間が掛かります。  

画面中央の「Welcome to O3DE」にある「Open...」ボタンを押して、色々なテストが動かせます。

以上
