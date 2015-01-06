package org.jglr.ns;

import java.io.*;

public class NSSourceFile
{

    private String      name;
    private InputStream input;
    private String      content;

    public NSSourceFile(File in) throws FileNotFoundException
    {
        this(in.getName(), in);
    }

    public NSSourceFile(String name, File in) throws FileNotFoundException
    {
        this(name, new FileInputStream(in));
    }

    public NSSourceFile(String name, InputStream in)
    {
        this.name = name;
        this.input = in;
    }

    public String name()
    {
        return name;
    }

    public InputStream inputStream()
    {
        return input;
    }

    public String content() throws IOException
    {
        if(content == null && input.available() > 0)
        {
            byte[] buffer = new byte[2048];
            int i;
            try
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while((i = input.read(buffer)) != -1)
                {
                    out.write(buffer, 0, i);
                }
                out.flush();
                out.close();
                content = new String(out.toByteArray(), "UTF-8");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        return content;
    }
}
