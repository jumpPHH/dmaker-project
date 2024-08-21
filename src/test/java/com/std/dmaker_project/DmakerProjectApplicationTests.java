package com.std.dmaker_project;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class DmakerProjectApplicationTests {

	final static int BUFFER_SIZE = 10;

	@Test
	void deploymentsSrcListTest() {

		// 날짜별 백업 디렉토리 생성을 위한 캘린더..
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

		// 백업 타겟 디렉토리 경로 지정..
		Path targetDirPath = Paths.get("C:\\Users\\ucube\\Documents\\Deployments" , sdf.format(cal.getTime()));

		// 파일 타켓 디렉토리 경로 지정..
		String deploySrcTxtPath = "C:\\Users\\ucube\\Documents\\Files\\deploymentsSrcList.txt";

		System.out.println("프로젝트 배포소스 복사 시작.");

		try( BufferedReader reader =
					 new BufferedReader(
							 new FileReader( deploySrcTxtPath )) ) {

			// 백업 타겟에 베포 디렉토리가 있으면 삭제..
			if(Files.exists(targetDirPath)){
				FileUtils.forceDelete(targetDirPath .toFile());
				System.out.println("프로젝트 배포 폴더 삭제");
				Thread.sleep(300);
			}

			// 없다면 백업 디렉토리 생성..
			FileUtils.forceMkdir(targetDirPath.toFile());

			// 베포할 소스 파일 갯수 초기화..
			int srcCnt = 1;
			String sourcePath;

			// 베포 소스 파일 경로 null 이 아닌 경우..
			while( (sourcePath = reader.readLine() ) != null){

				// 베포할 소스가 없으면 무시..
				if( sourcePath.isEmpty() ) continue;

				// 프로젝트 명을 추출..
				String projectNm = sourcePath.substring( sourcePath.indexOf("IdeaProjects") + "IdeaProjects".length() + 1 , sourcePath.lastIndexOf("\\") );

				if( sourcePath.indexOf("\\src") > -1 ){
					projectNm = sourcePath.substring( sourcePath.indexOf("IdeaProjects") + "IdeaProjects".length() + 1 , sourcePath.indexOf("\\src") );
				}

				System.out.println(projectNm);
				System.out.println(srcCnt + " 번째 복사할 원복 파일 : " + sourcePath);

				// 베포 소스 경로 지정..
				Path targetSrcPath = Paths.get( targetDirPath.toString() ,
						projectNm ,
						sourcePath.substring( sourcePath.indexOf(projectNm) + projectNm.length() )
				);

				File originSrc = new File(sourcePath);

				// 지정 경로에 디렉토리가 존재 하는지 확인..
				if( originSrc.isDirectory() ){
					FileUtils.forceMkdir(targetSrcPath.toFile());
				}else{

					// 만약 베포 소스 파일이 비어있으면 정지..
					if(!Files.exists(originSrc.toPath())){
						System.out.println("복사할 원복 파일이 존재하지 않습니다. 파일 목록 파일(" + deploySrcTxtPath + ")를 확인하세요.");
						break;
					}

					System.out.println(srcCnt + " 번째 복사된 파일 : " + targetSrcPath);

					// 지정 경로에 부모 디렉토리 생성..
					FileUtils.forceMkdir( targetSrcPath.getParent().toFile() );

					InputStream fis = new BufferedInputStream(new FileInputStream(originSrc) , BUFFER_SIZE);

					try( FileOutputStream fos =
								 new FileOutputStream( targetSrcPath.toFile() )
					) {

						byte[] buf = new byte[BUFFER_SIZE];

						int size;

						// 베포 파일 소스 ----------------------  [ 소스 파일 갯수 만큼 반복 ]
						while( ( size = fis.read(buf) ) != -1 ) fos.write( buf ,
								0 ,
								size
						);
					} catch (Exception e) {
						System.out.println("ERROR [Original file copy]Exception - " +  e);
					}
					// 베포 파일 소스 카운트..
					srcCnt++;
				}
			}
			System.out.println("총 복사된 파일 건 : " +  srcCnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
