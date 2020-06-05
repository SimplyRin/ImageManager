package net.simplyrin.imagemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.simplyrin.rinstream.RinStream;

/**
 * Created by SimplyRin on 2020/05/30.
 *
 * Copyright (c) 2020 SimplyRin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class Main {

	public static void main(String[] args) {
		new Main().run();
	}

	public void run() {
		new RinStream();

		this.initialization();
		new Thread(() -> task()).start();
	}

	private File _7ZIP = null;
	private File DOWNLOADS = null;
	private File IMAGES = null;
	private File MP4 = null;
	private File ZIP = null;
	private String ONCOPIED = null;
	private String ZIP_TYPE = null;
	private File ZIP_ARCHIVE = null;
	private File UNKNOWN = null;

	private int LOOP = 0;

	public void initialization() {
		System.out.println("Initializing...");

		File file = new File("config.json");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JsonObject jsonObject = new JsonObject();

			jsonObject.addProperty("7z.exe", "C:/Program Files/7-Zip/7z.exe");

			jsonObject.addProperty("Loop", 60);
			jsonObject.addProperty("Downloads", "C:/Users/%USERNAME/Downloads");
			jsonObject.addProperty("Images", "C:/Users/%USERNAME/Pictures/ImageManager/Images");
			jsonObject.addProperty("MP4", "C:/Users/%USERNAME/Pictures/ImageManager/MP4");
			jsonObject.addProperty("OnCopied", "keep");
			jsonObject.addProperty("Zip", "C:/Users/%USERNAME/Pictures/ImageManager/Zip");
			jsonObject.addProperty("Zip_Type", "default");
			jsonObject.addProperty("Zip_Archive", "C:/Users/%USERNAME/Pictures/ImageManager/Zip/Archive");
			jsonObject.addProperty("Unknown", "C:/Users/%USERNAME/Pictures/ImageManager/Unknown");

			String pretty = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);

			try {
				FileWriter fileWriter = new FileWriter(file);
				fileWriter.write(pretty);
				fileWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String lines = null;
		try {
			lines = Files.lines(Paths.get(file.getPath()), Charset.defaultCharset()).collect(Collectors.joining(System.getProperty("line.separator")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		JsonObject jsonObject = new JsonParser().parse(lines).getAsJsonObject();

		_7ZIP = new File(this.replacePathName(jsonObject.get("7z.exe").getAsString()));

		LOOP = jsonObject.get("Loop").getAsInt();

		DOWNLOADS = new File(this.replacePathName(jsonObject.get("Downloads").getAsString()));
		IMAGES = new File(this.replacePathName(jsonObject.get("Images").getAsString()));
		MP4 = new File(this.replacePathName(jsonObject.get("MP4").getAsString()));
		ONCOPIED = jsonObject.get("OnCopied").getAsString();
		ZIP = new File(this.replacePathName(jsonObject.get("Zip").getAsString()));
		ZIP_TYPE = jsonObject.get("Zip_Type").getAsString();
		ZIP_ARCHIVE = new File(this.replacePathName(jsonObject.get("Zip_Archive").getAsString()));
		UNKNOWN = new File(this.replacePathName(jsonObject.get("Unknown").getAsString()));

		System.out.println("Initialized.");
	}

	public void task() {
		while (true) {
			System.out.println("Checking download folder...");

			for (File file : DOWNLOADS.listFiles()) {
				String modifiedTime = this.getModifiedTime(file);

				if (modifiedTime.equals(this.getToday())) {
					String name = file.getName();

					if (name.endsWith("_orig.jpg") || name.endsWith("_orig.png")
							|| (name.split("-").length >= 2 && (name.endsWith("-vid1.mp4") || name.endsWith("-img.zip")))
							|| (name.endsWith("-media.zip") && name.contains("(") && name.contains(")") && name.contains("_"))) {
						File target = new File(this.getDirectory(name), name);
						if (!target.exists()) {
							System.out.println("Detected: " + file.getName());

							try {
								System.out.println("Copying... " + file.getName());
								FileUtils.copyFile(file, target);
								System.out.println("Copied " + file.getName());

								if (name.endsWith("-img.zip") || (name.endsWith("-media.zip") && name.contains("(") && name.contains(")"))) {
									File tFolder = new File(ZIP, FilenameUtils.getBaseName(name));

									String[] types = ZIP_TYPE.split(",");

									for (String type : types) {
										if (type.equals("each")) {
											tFolder = IMAGES;
										} else if (type.equalsIgnoreCase("raw")) {
											tFolder = ZIP;
										} else if (type.equalsIgnoreCase("username")) {
											tFolder = new File(ZIP, name.split("-")[0]);
										}

										tFolder.mkdirs();

										String qM = "\"";
										String command = qM + _7ZIP.getAbsolutePath() + qM + " x -y -o" + qM
												+ tFolder.getAbsolutePath() + qM + " " + qM + target.getAbsolutePath() + qM;

										System.out.println("Execution command: " + command);

										this.runCommand(command, null);

										System.out.println("Copied to " + tFolder.getAbsolutePath());
									}
								} else {
									System.out.println("Copied to " + target.getAbsolutePath());
								}

								if (ONCOPIED.equalsIgnoreCase("delete")) {
									System.out.println("OnCopied is set to delete, '" + file.getName() + "' is being deleted.");
									file.delete();
								}
							} catch (IOException e) {
								System.out.println("Failed copy: " + file.getName());
							}
						}
					}
				}
			}

			try {
				Thread.sleep(1000 * LOOP);
			} catch (Exception e) {
			}
		}
	}

	public String replacePathName(String path) {
		return path.replace("%USERNAME", System.getProperty("user.name"));
	}

	public File getDirectory(String name) {
		if (name.endsWith(".jpg") || name.endsWith(".png")) {
			return IMAGES;
		}
		if (name.endsWith(".mp4")) {
			return MP4;
		}
		if (name.endsWith(".zip")) {
			return ZIP_ARCHIVE;
		}
		return UNKNOWN;
	}

	public String getModifiedTime(File file) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Long lastModified = file.lastModified();
		return simpleDateFormat.format(lastModified);
	}

	public String getToday() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		return simpleDateFormat.format(new Date());
	}

	private void runCommand(String command, Callback callback) {
		final Process process;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
			processBuilder.redirectErrorStream(true);

			process = processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		new Thread(() -> {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			try {
				while ((line = bufferedReader.readLine()) != null) {
					if (callback != null) {
						callback.response(line);
					}
				}
			} catch (Exception e) {
			}
		}).start();

		new Thread(() -> {
			try {
				process.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (callback != null) {
				callback.response("taskEnded");
			}
		}).start();
	}

	public interface Callback {
		void response(String response);
	}

}
