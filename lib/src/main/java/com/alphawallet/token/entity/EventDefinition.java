package com.alphawallet.token.entity;

import com.alphawallet.token.tools.Numeric;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListSelectionModel;

/**
 * Created by JB on 21/03/2020.
 */
public class EventDefinition
{
    public ContractInfo contract;
    public String attributeName; //TransactionResult: method
    public NamedType type;
    public String filter;
    public String select;
<<<<<<< HEAD
    public BigInteger readBlock = BigInteger.ZERO;
=======
    public BigInteger readBlock;
>>>>>>> 5b797a144... parse activity code and populate layouts
    public Attribute parentAttribute;
    public String activityName = null;

    public String getFilterTopicValue()
    {
        // This regex splits up the "filterArgName=${filterValue}" directive and gets the 'filterValue'
        Matcher m = Pattern.compile("\\$\\{([^}]+)\\}").matcher(filter);
        return (m.find() && m.groupCount() >= 1) ? m.group(1) : null;
    }

    public String getFilterTopicIndex()
    {
        // Get the filter name from the directive and strip whitespace
        String[] item = filter.split("=");
        return item[0].replaceAll("\\s+", "");
    }

    public int getTopicIndex(String filterTopic)
    {
        if (type == null || filterTopic == null) return -1;
        return type.getTopicIndex(filterTopic);
    }

    public int getSelectIndex(boolean indexed)
    {
        int index = 0;
        boolean found = false;
        for (String label : type.getArgNames(indexed))
        {
            if (label.equals(select))
            {
                found = true;
                break;
            }
            else
            {
                index++;
            }
        }

        return found ? index : -1;
    }

    public int getEventChainId()
    {
        if (parentAttribute != null)
        {
            return parentAttribute.originContract.addresses.keySet().iterator().next();
        }
        else
        {
            return contract.addresses.keySet().iterator().next();
        }
    }

    public String getEventContractAddress()
    {
        int chainId = getEventChainId();
        String contractAddress;
        if (parentAttribute != null)
        {
            contractAddress = parentAttribute.originContract.addresses.get(chainId).get(0);
        }
        else
        {
            contractAddress = contract.addresses.get(chainId).get(0);
        }

        return contractAddress;
    }

    public int getNonIndexedIndex(String name)
    {
        if (type == null || name == null) return -1;
        return type.getNonIndexedIndex(name);
    }

    public boolean equals(EventDefinition ev)
    {
        if (contract.getfirstChainId() == ev.contract.getfirstChainId() && contract.getFirstAddress().equalsIgnoreCase(ev.contract.getFirstAddress()) &&
                filter.equals(ev.filter) && type.name.equals(ev.type.name) && (
                (activityName != null && ev.activityName != null && activityName.equals(ev.activityName)) ||
<<<<<<< HEAD
                        (attributeName != null && ev.attributeName != null && attributeName.equals(ev.attributeName))))
=======
                (attributeName != null && ev.attributeName != null && attributeName.equals(ev.attributeName))) )
>>>>>>> 5b797a144... parse activity code and populate layouts
        {
            return true;
        }
        else
        {
            return false;
        }
    }
<<<<<<< HEAD

    public String getEventKey()
    {
        return getEventKey(contract.getfirstChainId(), contract.getFirstAddress(), activityName, attributeName);
    }

    public static String getEventKey(int chainId, String eventAddress, String activityName, String attributeName)
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(intToByteArray(chainId));
            digest.update(eventAddress.getBytes());
            if (activityName != null) digest.update(activityName.getBytes());
            if (attributeName != null) digest.update(attributeName.getBytes());

            byte[] bytes = digest.digest();
            for (byte aByte : bytes)
            {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private static byte[] intToByteArray(int a)
    {
        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }
}
=======
}
>>>>>>> 5b797a144... parse activity code and populate layouts
