# 贡献代码
CRPC基于Apache 2.0协议发布，遵循标准Github开发流程，使用Github管理议题。如果你想共享代码，请遵循如下文档内容。

## 协议
如果你想贡献代码，请务必遵循Apache 2.0协议。

## 代码风格
提交PR的时候，以下要求是不是必需的，但是遵循这些约定会对项目发展更有益处。

1. CRPC项目提供了一个代码格式化的文件[conf/crpc-checkstyle.xml],基于Eclipse格式化，项目打包的时候，会使用它自动格式化代码文件。

2. 每个`.java`文件都需要写Javadoc注释，用来标识这个类的作用是什么，还需要有一个作者标识`@author`。

3. 每个`.java`文件都需要添加ASF协议头注释，在项目打包的时候会自动添加，你也可以在IDE配置里自动配置。

4. 当年修改一个`.java`文件的时候，需要添加一个`@author`标签，用来标识你也是这个文件的作者。

5. 添加Javadoc注释是一个好习惯，你需要用这种方式来帮助读者理解。

6. 记得写单元测试，保证代码的健壮性。

7. 怎么写提交信息？(https://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html)