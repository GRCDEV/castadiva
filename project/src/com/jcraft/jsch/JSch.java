/* -*-mode:java; c-basic-offset:2; indent-tabs-mode:nil -*- */
/*
Copyright (c) 2002,2003,2004,2005,2006 ymnk, JCraft,Inc. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

  1. Redistributions of source code must retain the above copyright notice,
     this list of conditions and the following disclaimer.

  2. Redistributions in binary form must reproduce the above copyright 
     notice, this list of conditions and the following disclaimer in 
     the documentation and/or other materials provided with the distribution.

  3. The names of the authors may not be used to endorse or promote products
     derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.jcraft.jsch;

import java.io.InputStream;
public class JSch{
  static java.util.Hashtable config=new java.util.Hashtable();
  static{
//  config.put("kex", "diffie-hellman-group-exchange-sha1");
    config.put("kex", "diffie-hellman-group1-sha1,diffie-hellman-group-exchange-sha1");
    config.put("server_host_key", "ssh-rsa,ssh-dss");
    //config.put("server_host_key", "ssh-dss,ssh-rsa");

    config.put("cipher.s2c", "3des-cbc,blowfish-cbc");
    config.put("cipher.c2s", "3des-cbc,blowfish-cbc");

    config.put("mac.s2c", "hmac-md5,hmac-sha1,hmac-sha1-96,hmac-md5-96");
    config.put("mac.c2s", "hmac-md5,hmac-sha1,hmac-sha1-96,hmac-md5-96");
    config.put("compression.s2c", "none");
    config.put("compression.c2s", "none");
    config.put("lang.s2c", "");
    config.put("lang.c2s", "");

    config.put("compression_level", "6");

    config.put("diffie-hellman-group-exchange-sha1", 
                                "com.jcraft.jsch.DHGEX");
    config.put("diffie-hellman-group1-sha1", 
	                        "com.jcraft.jsch.DHG1");

    config.put("dh",            "com.jcraft.jsch.jce.DH");
    config.put("3des-cbc",      "com.jcraft.jsch.jce.TripleDESCBC");
    config.put("blowfish-cbc",  "com.jcraft.jsch.jce.BlowfishCBC");
    config.put("hmac-sha1",     "com.jcraft.jsch.jce.HMACSHA1");
    config.put("hmac-sha1-96",  "com.jcraft.jsch.jce.HMACSHA196");
    config.put("hmac-md5",      "com.jcraft.jsch.jce.HMACMD5");
    config.put("hmac-md5-96",   "com.jcraft.jsch.jce.HMACMD596");
    config.put("sha-1",         "com.jcraft.jsch.jce.SHA1");
    config.put("md5",           "com.jcraft.jsch.jce.MD5");
    config.put("signature.dss", "com.jcraft.jsch.jce.SignatureDSA");
    config.put("signature.rsa", "com.jcraft.jsch.jce.SignatureRSA");
    config.put("keypairgen.dsa",   "com.jcraft.jsch.jce.KeyPairGenDSA");
    config.put("keypairgen.rsa",   "com.jcraft.jsch.jce.KeyPairGenRSA");
    config.put("random",        "com.jcraft.jsch.jce.Random");

    config.put("none",           "com.jcraft.jsch.CipherNone");

    config.put("aes128-cbc",    "com.jcraft.jsch.jce.AES128CBC");
    config.put("aes192-cbc",    "com.jcraft.jsch.jce.AES192CBC");
    config.put("aes256-cbc",    "com.jcraft.jsch.jce.AES256CBC");
//  config.put("cipher.s2c", "aes128-cbc,3des-cbc,blowfish-cbc");
//  config.put("cipher.c2s", "aes128-cbc,3des-cbc,blowfish-cbc");

    config.put("zlib",          "com.jcraft.jsch.jcraft.Compression");

    config.put("StrictHostKeyChecking",  "ask");
  }
  java.util.Vector pool=new java.util.Vector();
  java.util.Vector identities=new java.util.Vector();
  //private KnownHosts known_hosts=null;
  private HostKeyRepository known_hosts=null;

  public JSch(){
    //known_hosts=new KnownHosts(this);
  }

  public Session getSession(String username, String host) throws JSchException { return getSession(username, host, 22); }
  public Session getSession(String username, String host, int port) throws JSchException {
    Session s=new Session(this); 
    s.setUserName(username);
    s.setHost(host);
    s.setPort(port);
    pool.addElement(s);
    return s;
  }

  protected boolean removeSession(Session session){
    synchronized(pool){
      return pool.remove(session);
    }
  }
  public void setHostKeyRepository(HostKeyRepository foo){
    known_hosts=foo;
  }
  public void setKnownHosts(String foo) throws JSchException{
    if(known_hosts==null) known_hosts=new KnownHosts(this);
    if(known_hosts instanceof KnownHosts){
      synchronized(known_hosts){
	((KnownHosts)known_hosts).setKnownHosts(foo); 
      }
    }
  }
  public void setKnownHosts(InputStream foo) throws JSchException{ 
    if(known_hosts==null) known_hosts=new KnownHosts(this);
    if(known_hosts instanceof KnownHosts){
      synchronized(known_hosts){
	((KnownHosts)known_hosts).setKnownHosts(foo); 
      }
    }
  }
  /*
  HostKeyRepository getKnownHosts(){ 
    if(known_hosts==null) known_hosts=new KnownHosts(this);
    return known_hosts; 
  }
  */
  public HostKeyRepository getHostKeyRepository(){ 
    if(known_hosts==null) known_hosts=new KnownHosts(this);
    return known_hosts; 
  }
  /*
  public HostKey[] getHostKey(){
    if(known_hosts==null) return null;
    return known_hosts.getHostKey(); 
  }
  public void removeHostKey(String foo, String type){
    removeHostKey(foo, type, null);
  }
  public void removeHostKey(String foo, String type, byte[] key){
    if(known_hosts==null) return;
    known_hosts.remove(foo, type, key); 
  }
  */
  public void addIdentity(String foo) throws JSchException{
    addIdentity(foo, (String)null);
  }
  public void addIdentity(String foo, String bar) throws JSchException{
    Identity identity=new IdentityFile(foo, this);
    if(bar!=null){
      identity.setPassphrase(bar);
    }
    synchronized(identities){
      if(!identities.contains(identity)){
	identities.addElement(identity);
      }
    }
  }
  String getConfig(String foo){ return (String)(config.get(foo)); }

  private java.util.Vector proxies;
  void setProxy(String hosts, Proxy proxy){
    java.lang.String[] patterns=Util.split(hosts, ",");
    if(proxies==null){proxies=new java.util.Vector();}
    synchronized(proxies){
      for(int i=0; i<patterns.length; i++){
	if(proxy==null){
	  proxies.insertElementAt(null, 0);
	  proxies.insertElementAt(patterns[i].getBytes(), 0);
	}
	else{
	  proxies.addElement(patterns[i].getBytes());
	  proxies.addElement(proxy);
	}
      }
    }
  }
  Proxy getProxy(String host){
    if(proxies==null)return null;
    byte[] _host=host.getBytes();
    synchronized(proxies){
      for(int i=0; i<proxies.size(); i+=2){
	if(Util.glob(((byte[])proxies.elementAt(i)), _host)){
	  return (Proxy)(proxies.elementAt(i+1));
	}
      }
    }
    return null;
  }
  void removeProxy(){
    proxies=null;
  }

  public static void setConfig(java.util.Hashtable foo){
    synchronized(config){
      for(java.util.Enumeration e=foo.keys() ; e.hasMoreElements() ;) {
	String key=(String)(e.nextElement());
	config.put(key, (String)(foo.get(key)));
      }
    }
  }
}
