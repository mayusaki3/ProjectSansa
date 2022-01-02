# o3deメモ

Project Sansaでは、ゲームエンジンとして Open 3D Engine (o3de) を使用します。  
以下は、利用可能にするまでの、簡単なメモです。  
なお、実行は Windows 11 Pro で行っています。

# ツール類のインストール
- Python, cmake関連  
Visual Studio 2022 Community インストール時に、以下を選択してください。  
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

# o3de のダウンロード
以下のバッチを実行してください。 
src/GameEngine/ 内   
```
1_o3de_download.bat
```
src/GameEngine/o3de/ にダウンロードされます。  
2回目以降はpullします。

# o3de のビルド
スタートメニューから以下を起動します。
```
Visual Studio 2022
  x64 Native Tools Command Prompt for VS 2022 Current
```

最初にゲームエンジンのビルドを行います。  
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

# エディタとアセットビルダの作成
エディタとアセットビルダのビルドを行います。  
先ほどと同様にに以下のバッチをドラッグして実行してください。   
src/GameEngine/ 内   
```
4_o3de_build_manager.bat
```
エラーがある場合は、o3deフォルダを削除して最初からやり直してください。  
成功すれば、以下のファイルができているので、実行してみましょう。  
src\GameEngine\o3de\bin\profile\o3de.exe  
先ほどと同様に以下のバッチをドラッグして実行してください。  
（cmakeへのパスが通っていれば直接でもOKです）
```
5_o3de_run.bat
```

以上
