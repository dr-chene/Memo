### 考核项目memo：仿华为备忘录

**语言：** kotlin

**第三方库：** glide,koin(依赖注入库)，gson

**实现功能：** 添加、保存、删除笔记，搜索本地笔记，笔记分类，笔记编辑样式改变，插入图片

**已知未解决bug：** 加载笔记时插入的图片可能会显示多张，设置对齐方式无效，删除未实现回退，改变分类回到主页时笔记item的背景色不会立即改变，且图片可能会加载失败。编辑选择文字颜色时会出现选中多个颜色的情况。以及笔记富文本编辑是我临时写的，其中存在许多需要斟酌的地方，也可能存在我未发现的bug

**新发现bug（之前没有，为了上传演示图片发现的新bug，时间不多，会尽量改）：** ~~文字编辑样式选择窗口无法响应选择颜色的点击事件，事件会穿透到下方~~ (已解决)

**演示图片：**  ![](https://github.com/dr-chene/Memo/blob/main/Screenshot_20201201_191833_com.example.memo.jpg) 
               ![](https://github.com/dr-chene/Memo/blob/main/Screenshot_20201201_191618_com.example.memo.jpg)
               ![](https://github.com/dr-chene/Memo/blob/main/Screenshot_20201201_202430_com.example.memo.jpg)
