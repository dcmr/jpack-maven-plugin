package com.blinkfox.jpack;

import static com.blinkfox.jpack.PackBuildMojo.HOME_DIR_NAME;

import com.blinkfox.jpack.utils.Logger;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.MojoRule;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * 按顺序执行各个单元测试方法.
 *
 * @author blinkfox on 2019-05-03.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PackBuildMojoTest {

    /**
     * MojoRule.
     */
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() {
            Logger.initSetLog(new SystemStreamLog());
        }

        @Override
        protected void after() {
            // no code.
        }
    };

    /**
     * 测试 jpack 插件 build 目标的全功能场景.
     *
     * @throws Exception 异常
     */
    @Test
    public void testExecute1() throws Exception {
        // 获取测试的 pom.xml
        String baseDir = PlexusTestCase.getBasedir();
        File packPom = new File(baseDir, "src/test/resources/jpack-test-pom.xml");
        Assert.assertTrue(packPom.exists());
        this.copyDockerTestFiles(baseDir);

        // 获取 mojo 并执行.
        PackBuildMojo packMojo = (PackBuildMojo) rule.lookupMojo("build", packPom);
        Assert.assertNotNull(packMojo);
        this.setPackMojoProperties(baseDir, packMojo);
        packMojo.execute();
    }

    /**
     * 测试 jpack 插件 build 目标的无平台简单场景.
     *
     * @throws Exception 异常
     */
    @Test
    public void testExecute2WithoutPlatforms() throws Exception {
        // 获取测试的 pom.xml
        String baseDir = PlexusTestCase.getBasedir();
        final PackBuildMojo packMojo = (PackBuildMojo) rule.lookupMojo("build",
                new File(baseDir, "src/test/resources/jpack-test-pom-simple.xml"));
        this.copyDockerTestFiles(baseDir);

        // 预先生成一些目录.
        File targetDir = new File(baseDir + File.separator + "target");
        String homeDir = targetDir.getAbsolutePath() + File.separator + HOME_DIR_NAME;
        FileUtils.mkdir(homeDir);
        String platformPath = homeDir + File.separator + "windows";
        FileUtils.mkdir(platformPath);

        // 设置一些属性.
        this.setPackMojoProperties(baseDir, packMojo);

        // 执行该 mojo.
        packMojo.execute();
    }

    /**
     * 测试 jpack 插件 docker 的构建.
     *
     * @throws Exception 异常
     */
    @Test
    public void testExecute3WithDocker() throws Exception {
        // 获取测试的 pom.xml
        String baseDir = PlexusTestCase.getBasedir();
        File packPom = new File(baseDir, "src/test/resources/jpack-test-docker.xml");
        Assert.assertTrue(packPom.exists());
        this.copyDockerTestFiles(baseDir);

        // 获取 mojo 并执行.
        PackBuildMojo dockerBuildMojo = (PackBuildMojo) rule.lookupMojo("build", packPom);
        Assert.assertNotNull(dockerBuildMojo);
        this.setPackMojoProperties(baseDir, dockerBuildMojo);
        dockerBuildMojo.execute();
    }

    /**
     * 测试 jpack 插件 自定义 Dockerfile 的构建.
     *
     * @throws Exception 异常
     */
    @Test
    public void testExecute4WithCustomDockerfile() throws Exception {
        // 获取测试的 pom.xml
        String baseDir = PlexusTestCase.getBasedir();
        File packPom = new File(baseDir, "src/test/resources/jpack-test-docker.xml");
        Assert.assertTrue(packPom.exists());
        this.copyDockerTestFiles(baseDir);
        FileUtils.copyFileToDirectory("src/test/resources/dockers/Dockerfile", baseDir);

        // 获取 mojo 并执行.
        PackBuildMojo dockerBuildMojo = (PackBuildMojo) rule.lookupMojo("build", packPom);
        Assert.assertNotNull(dockerBuildMojo);
        dockerBuildMojo.getDocker().setDockerfile("Dockerfile");
        this.setPackMojoProperties(baseDir, dockerBuildMojo);
        dockerBuildMojo.execute();

        // 执行结束后删除根目录下的 Dockerfile 文件.
        FileUtils.forceDelete(baseDir + File.separator + "Dockerfile");
    }

    /**
     * 设置 PackBuildMojo 对象的一些用于测试的属性值.
     *
     * @param baseDir 项目基础目录
     * @param packMojo PackBuildMojo 对象
     */
    private void setPackMojoProperties(String baseDir, AbstractBaseMojo packMojo) {
        packMojo.setTargetDir(new File(baseDir + File.separator + "target"));
        packMojo.setGroupId("blinkfox");
        packMojo.setArtifactId("jpack-test");
        packMojo.setVersion("1.0.0-SNAPSHOT");
        packMojo.setFinalName("jpack-test-1.0.0-SNAPSHOT");
        packMojo.setDescription("这是 jpack-test 项目的测试 description.");
    }

    /**
     * 复制测试 Docker 相关的一些文件.
     */
    private void copyDockerTestFiles(String baseDir) throws IOException {
        FileUtils.copyFileToDirectory("src/test/resources/dockers/jpack-test-1.0.0-SNAPSHOT.jar",
                baseDir + File.separator + "target");
    }

}
