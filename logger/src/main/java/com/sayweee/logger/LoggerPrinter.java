package com.sayweee.logger;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


class LoggerPrinter implements Printer {

    private final ThreadLocal<Boolean> localEnable = new ThreadLocal<>();

    private final ThreadLocal<LogAdapter> localAdapter = new ThreadLocal<>();

    private final List<LogAdapter> logAdapters = new ArrayList<>();

    private String localSpace;
    private String localTag;
    private String localFile;

    @Override
    public Printer t(String tag) {
        if (tag != null) {
            localTag = tag;
        }
        return this;
    }

    @Override
    public Printer f(String fileName) {
        if (!Utils.isEmpty(fileName)) {
            localFile = fileName;
        }
        return this;
    }

    @Override
    public Printer s(String space) {
        if (space != null) {
            localSpace = space;
        }
        return this;
    }

    @Override
    public Printer enable(boolean enable) {
        localEnable.set(enable);
        return this;
    }

    @Override
    public Printer adapter(LogAdapter adapter) {
        localAdapter.set(adapter);
        return this;
    }

    @Override
    public Printer addAdapter(LogAdapter adapter) {
        if (adapter != null) {
            logAdapters.add(adapter);
        }
        return this;
    }

    @Override
    public Printer removeAdapter(LogAdapter adapter) {
        logAdapters.remove(adapter);
        return this;
    }

    @Override
    public Printer clearAdapters() {
        logAdapters.clear();
        return this;
    }

    @Override
    public void v(Object arg) {
        v(null, arg);
    }

    @Override
    public void v(String tag, Object... args) {
        log(Logger.VERBOSE, tag, args);
    }

    @Override
    public void d(Object arg) {
        d(null, arg);
    }

    @Override
    public void d(String tag, Object... args) {
        log(Logger.DEBUG, tag, args);
    }

    @Override
    public void i(Object arg) {
        i(null, arg);
    }

    @Override
    public void i(String tag, Object... args) {
        log(Logger.INFO, tag, args);
    }

    @Override
    public void w(Object arg) {
        w(null, arg);
    }

    @Override
    public void w(String tag, Object... args) {
        log(Logger.WARN, tag, args);
    }

    @Override
    public void error(Throwable throwable) {
        error(null, throwable);
    }


    @Override
    public void error(String tag, Throwable throwable) {
        log(Logger.ERROR, tag, Utils.getStackTraceString(throwable));
    }

    @Override
    public void e(Object arg) {
        e(null, arg);
    }

    @Override
    public void e(String tag, Object... args) {
        log(Logger.ERROR, tag, args);
    }

    @Override
    public void json(Object arg) {
        json(null, arg);
    }

    @Override
    public void json(String tag, Object... args) {
        log(Logger.INFO, tag, createJson(args));
    }

    @Override
    public void toJson(Object obj) {
        toJson(null, obj);
    }

    @Override
    public void toJson(String tag, Object obj) {
        log(Logger.INFO, tag, Utils.toJson(obj));
    }


    @Override
    public void xml(Object obj) {
        if (obj != null && obj instanceof String) {
            String xml = (String) obj;
            if (Utils.isEmpty(xml)) {
                i("Empty/Null xml content");
                return;
            }
            try {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                i(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
            } catch (TransformerException e) {
                i("Invalid xml", obj);
            }
        } else {
            e(null, obj);
        }
    }

    private String getSpace() {
        String space = "\r\n";
        if (localSpace != null) {
            space = localSpace;
            localSpace = null;
        }
        return space;
    }

    private String getTag() {
        String tag = null;
        if (localTag != null) {
            tag = localTag;
            localTag = null;
        }
        return tag;
    }

    private boolean getOnceEnable() {
        Boolean bool = localEnable.get();
        if (bool == null) {
            return true;
        }
        localEnable.remove();
        return bool;
    }

    private String getFileName() {
        String fileName = null;
        if (!Utils.isEmpty(localFile)) {
            fileName = localFile;
            localFile = null;
        }
        return fileName;
    }

    private LogAdapter getOnceAdapter() {
        LogAdapter adapter = localAdapter.get();
        if (adapter == null) {
            return null;
        }
        localAdapter.remove();
        return adapter;
    }

    public void log(int priority, String tag, Object... args) {
        print(priority, tag, createMessage(args));
    }

    /**
     * 获取tag
     *
     * @param tag
     * @return
     */
    protected String getTag(String tag) {
        String extraTag = getTag();
        if (!Utils.isEmpty(tag) && !Utils.isEmpty(extraTag)) {
            return tag + " --> " + extraTag;
        }
        return Utils.isEmpty(tag) ? Logger.TAG : tag;
    }

    public void print(int priority, String tag, String msg) {
        if (Logger.sEnable && getOnceEnable()) {
            tag = getTag(tag);  //tag
            msg = Utils.isEmpty(msg) ? "" : msg;
            String extra = getFunctionName();
            String name = getFileName();
            LogAdapter onceAdapter = getOnceAdapter();
            if (onceAdapter != null) { //一次性使用的adapter
                if (onceAdapter.isLoggable(priority, tag)) {
                    onceAdapter.log(priority, tag, name, msg, extra);
                }
            } else if (logAdapters != null && logAdapters.size() > 0) { //添加的adapter
                for (LogAdapter adapter : logAdapters) {
                    if (adapter.isLoggable(priority, tag)) {
                        adapter.log(priority, tag, name, msg, extra);
                    }
                }
            } else { //使用内置输出方式
                if (DefaultLogAdapter.newInstance().isLoggable(priority, tag)) {
                    DefaultLogAdapter.newInstance().log(priority, tag, name, msg, extra);
                }
                //未设置默认的输出适配器 使用内置文件输出
                if (!Utils.isEmpty(name) && FileLogAdapter.newInstance().isLoggable(priority, tag)) {
                    FileLogAdapter.newInstance().log(priority, tag, name, msg, extra);
                }
            }
        }
    }

    public String createJson(Object... args) {
        String space = getSpace();
        StringBuilder builder = new StringBuilder();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                builder.append(Utils.toJsonString(args[i]));
                if (i != args.length - 1) {
                    builder.append(space);
                }
            }
        }
        return builder.toString();
    }

    public String createMessage(Object... args) {
        String space = getSpace();
        StringBuilder builder = new StringBuilder();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                builder.append(Utils.toString(args[i]));
                if (i != args.length - 1) {
                    builder.append(space);
                }
            }
        }
        return builder.toString();
    }


    public String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts != null && sts.length > 0) {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(Logger.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(LoggerPrinter.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(Utils.class.getName())) {
                    continue;
                }

                return "[Thread : " + Thread.currentThread().getName() + "] " + "(" + st.getFileName() + ":" + st.getLineNumber() + ")";
            }
        }
        return "";
    }
}
